package mir.reactions.amalthea2ascet;

import java.util.Collections;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

@SuppressWarnings("all")
public class Amalthea2ascetChangePropagationSpecification extends AbstractReactionsChangePropagationSpecification
        implements ChangePropagationSpecification {
    public Amalthea2ascetChangePropagationSpecification() {
        super(
                MetamodelDescriptor.with(Collections.singleton("http://vitruv.tools/reactionsparser/model2")),
                MetamodelDescriptor.with(Collections.singleton("http://vitruv.tools/reactionsparser/model")));
    }

    protected RoutinesFacadesProvider createRoutinesFacadesProvider(final ReactionExecutionState executionState) {
        return new mir.routines.amalthea2ascet.Amalthea2ascetRoutinesFacadesProvider(executionState);
    }

    protected void setup() {
        org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.putIfAbsent(
                tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl.eNS_URI,
                tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl.eINSTANCE);
        org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.putIfAbsent(
                tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.eNS_URI,
                tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.eINSTANCE);
        addReaction(new mir.reactions.amalthea2ascet.ComponentContainerInsertedAsRootReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("amalthea2ascet"))));
        addReaction(new mir.reactions.amalthea2ascet.TaskDeletedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("amalthea2ascet"))));
        addReaction(new mir.reactions.amalthea2ascet.TaskCreatedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("amalthea2ascet"))));
    }
}
