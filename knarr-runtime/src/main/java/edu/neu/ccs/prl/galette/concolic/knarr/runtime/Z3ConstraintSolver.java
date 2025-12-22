package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import com.microsoft.z3.*;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.VisitorException;
import za.ac.sun.cs.green.service.z3.Z3JavaTranslator;

/**
 * Z3-based constraint solver for symbolic execution.
 *
 * This solver uses Microsoft Z3 SMT solver to handle complex constraints
 * that the simple range-based solver cannot handle.
 *
 * @purpose Drop-in replacement for ConstraintSolver using Z3
 * @feature Full SMT constraint solving
 * @feature Automatic fallback to simple solver
 * @feature Configurable timeout
 */
public class Z3ConstraintSolver {

    private static final boolean DEBUG = Boolean.getBoolean("z3.solver.debug");
    private static final boolean USE_Z3 = Boolean.getBoolean("use.z3.solver");
    private static final int Z3_TIMEOUT_MS = Integer.getInteger("z3.timeout.ms", 10000); // 10s default
    private static final boolean FALLBACK_TO_SIMPLE = Boolean.getBoolean("z3.fallback.simple");

    static {
        if (DEBUG) {
            System.out.println("[Z3ConstraintSolver] Configuration:");
            System.out.println("  USE_Z3: " + USE_Z3);
            System.out.println("  Z3_TIMEOUT_MS: " + Z3_TIMEOUT_MS);
            System.out.println("  FALLBACK_TO_SIMPLE: " + FALLBACK_TO_SIMPLE);
        }
    }

    /**
     * Main entry point - drop-in replacement for ConstraintSolver.solveConstraint()
     *
     * @param constraint The constraint expression to solve
     * @return InputSolution with variable assignments, or null if UNSAT
     */
    public static InputSolution solveConstraintWithZ3(Expression constraint) {
        if (constraint == null) {
            return null;
        }

        if (!USE_Z3) {
            // Z3 disabled, use simple solver
            if (DEBUG) System.out.println("[Z3ConstraintSolver] Z3 disabled, using simple solver");
            return ConstraintSolver.solveConstraint(constraint);
        }

        if (DEBUG) {
            System.out.println("[Z3ConstraintSolver] Solving constraint with Z3:");
            System.out.println("  " + constraint.toString());
        }

        try {
            // Try Z3 solver
            InputSolution solution = solveWithZ3(constraint);

            if (solution != null && solution.isSatisfiable()) {
                if (DEBUG) {
                    System.out.println("[Z3ConstraintSolver] Z3 found solution: " + solution);
                }
                return solution;
            } else {
                if (DEBUG) {
                    System.out.println("[Z3ConstraintSolver] Z3 returned UNSAT");
                }
                return null;
            }

        } catch (Exception e) {
            if (DEBUG) {
                System.err.println("[Z3ConstraintSolver] Z3 solver failed: " + e.getMessage());
                e.printStackTrace();
            }

            if (FALLBACK_TO_SIMPLE) {
                if (DEBUG) System.out.println("[Z3ConstraintSolver] Falling back to simple solver");
                return ConstraintSolver.solveConstraint(constraint);
            } else {
                // No fallback, return UNSAT
                return null;
            }
        }
    }

    /**
     * Solve constraint using Z3 SMT solver.
     * Adapted from Z3Worker.solve() method.
     *
     * @param constraint The Green expression constraint
     * @return InputSolution with SAT assignments, or null if UNSAT
     */
    private static InputSolution solveWithZ3(Expression constraint) throws Exception {
        // Wrap constraint in a map (Z3Worker expects Map<String, Expression>)
        Map<String, Expression> constraints = new HashMap<>();
        constraints.put("c0", constraint);

        // Result containers
        ArrayList<AbstractMap.SimpleEntry<String, Object>> sat = new ArrayList<>();
        HashSet<String> unsat = new HashSet<>();

        // Create Z3 context and solve
        Context ctx = new Context();
        try {
            // Translate Green expressions to Z3
            HashSet<Expr> vars = new HashSet<>();
            HashSet<FuncDecl> functions = new HashSet<>();
            Map<String, BoolExpr> constraintsInZ3 = translate(constraints, vars, functions, ctx);

            // Create Z3 solver
            Solver solver = ctx.mkSolver();

            // Set timeout
            Params p = ctx.mkParams();
            p.add("timeout", Z3_TIMEOUT_MS);
            solver.setParameters(p);

            // Add constraints
            for (Map.Entry<String, BoolExpr> e : constraintsInZ3.entrySet()) {
                solver.assertAndTrack(e.getValue(), ctx.mkBoolConst(e.getKey()));
            }

            if (DEBUG) {
                System.out.println("[Z3ConstraintSolver] Z3 constraints added, checking satisfiability...");
            }

            // Solve
            long startTime = System.currentTimeMillis();
            Status result = solver.check();
            long solvingTime = System.currentTimeMillis() - startTime;

            if (DEBUG) {
                System.out.println("[Z3ConstraintSolver] Z3 result: " + result + " (took " + solvingTime + " ms)");
            }

            if (Status.SATISFIABLE == result) {
                // Extract model
                Model model = solver.getModel();

                // Extract function values (if any)
                for (FuncDecl decl : functions) {
                    if (model.getFuncInterp(decl) != null) {
                        FuncInterp z3Val = model.getFuncInterp(decl);
                        int[] funcRes = new int[z3Val.getNumEntries()];
                        for (FuncInterp.Entry e : z3Val.getEntries()) {
                            if (e.getArgs()[0].isIntNum() && e.getValue().isBV()) {
                                Long arg = ((IntNum) e.getArgs()[0]).getInt64();
                                Long res = ((BitVecNum) e.getValue()).getLong();
                                if (arg.intValue() >= 0 && arg.intValue() < funcRes.length) {
                                    funcRes[arg.intValue()] = res.intValue();
                                }
                            }
                        }
                        sat.add(new AbstractMap.SimpleEntry<>(decl.getName().toString(), funcRes));
                    }
                }

                // Extract variable values
                for (Expr z3Var : vars) {
                    Expr z3Val = model.evaluate(z3Var, true);
                    Object val = extractValue(z3Val);
                    if (val != null) {
                        sat.add(new AbstractMap.SimpleEntry<>(z3Var.toString(), val));
                        if (DEBUG) {
                            System.out.println("[Z3ConstraintSolver]   Variable: " + z3Var + " = " + val);
                        }
                    }
                }

                // Convert to InputSolution
                return convertToInputSolution(sat);

            } else if (Status.UNSATISFIABLE == result) {
                // UNSAT
                BoolExpr[] unsatCore = solver.getUnsatCore();
                for (BoolExpr e : unsatCore) {
                    String key = e.toString();
                    if (key.startsWith("|") && key.endsWith("|")) {
                        key = key.substring(1, key.length() - 1);
                    }
                    unsat.add(key);
                }

                if (DEBUG) {
                    System.out.println("[Z3ConstraintSolver] UNSAT core: " + unsat);
                }

                return null; // UNSAT

            } else {
                // UNKNOWN or timeout
                if (DEBUG) {
                    System.out.println("[Z3ConstraintSolver] Z3 returned UNKNOWN (possibly timeout)");
                }
                return null;
            }

        } finally {
            ctx.close();
            Native.finalizeMemory();
        }
    }

    /**
     * Translate Green expressions to Z3 expressions.
     * Adapted from Z3Worker.translate() method.
     *
     * @param constraints Map of constraint names to Green expressions
     * @param variables Output set of Z3 variables
     * @param functions Output set of Z3 functions
     * @param ctx Z3 context
     * @return Map of constraint names to Z3 boolean expressions
     */
    private static Map<String, BoolExpr> translate(
            Map<String, Expression> constraints, HashSet<Expr> variables, HashSet<FuncDecl> functions, Context ctx)
            throws VisitorException {

        Z3JavaTranslator translator = new Z3JavaTranslator(ctx);
        Map<String, BoolExpr> ret = new HashMap<>();

        for (Map.Entry<String, Expression> e : constraints.entrySet()) {
            e.getValue().accept(translator);
            ret.put(e.getKey(), (BoolExpr) translator.getTranslation());
        }

        variables.addAll(translator.getVariableMap().values());
        functions.addAll(translator.getFunctions().values());

        if (DEBUG) {
            System.out.println("[Z3ConstraintSolver] Translated to Z3:");
            System.out.println("  Variables: " + variables.size());
            System.out.println("  Functions: " + functions.size());
        }

        return ret;
    }

    /**
     * Extract concrete value from Z3 expression.
     * Adapted from Z3Worker.solve() lines 1091-1125.
     *
     * @param z3Val Z3 value expression
     * @return Concrete value (Integer, Long, Double, or String)
     */
    private static Object extractValue(Expr z3Val) {
        if (z3Val.isIntNum()) {
            // Integer value
            return Long.parseLong(z3Val.toString());
        } else if (z3Val.isBV()) {
            // Bit-vector value
            BitVecNum bv = (BitVecNum) z3Val;
            if (bv.getSortSize() == 64) {
                // Long
                BigInteger bi = bv.getBigInteger();
                return bi.longValue();
            } else {
                // Int
                Long l = bv.getLong();
                return l.intValue();
            }
        } else if (z3Val.isRatNum()) {
            // Rational/Real value
            RatNum rt = (RatNum) z3Val;
            return ((double) rt.getNumerator().getInt64())
                    / ((double) rt.getDenominator().getInt64());
        } else {
            // String value
            String sval = z3Val.toString();
            // Clean up string encoding
            Pattern p = Pattern.compile("\\\\x(\\d\\d)");
            Matcher m = p.matcher(sval);
            while (m.find()) {
                int i = Long.decode("0x" + m.group(1)).intValue();
                sval = sval.replace(m.group(0), String.valueOf((char) i));
            }
            return sval;
        }
    }

    /**
     * Convert Z3 SAT result to InputSolution.
     *
     * @param sat List of variable assignments from Z3
     * @return InputSolution with variable bindings
     */
    private static InputSolution convertToInputSolution(ArrayList<AbstractMap.SimpleEntry<String, Object>> sat) {
        InputSolution solution = new InputSolution();
        solution.setSatisfiable(true);

        for (AbstractMap.SimpleEntry<String, Object> entry : sat) {
            String varName = entry.getKey();
            Object value = entry.getValue();

            // Skip function values (arrays) - we only need simple variable values
            if (value instanceof int[]) {
                continue;
            }

            solution.setValue(varName, value);
        }

        return solution;
    }

    /**
     * Negate a constraint (reuse from ConstraintSolver).
     * This is the same logic, just exposed here for completeness.
     */
    public static Expression negateConstraint(Expression constraint) {
        return ConstraintSolver.negateConstraint(constraint);
    }
}
