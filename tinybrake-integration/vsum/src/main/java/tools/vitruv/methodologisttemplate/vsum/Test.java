package tools.vitruv.methodologisttemplate.vsum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import mir.reactions.model2Model2.Model2Model2ChangePropagationSpecification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.testutils.TestUserInteraction;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;
import tools.vitruv.methodologisttemplate.model.model.BrakeSystem;
import tools.vitruv.methodologisttemplate.model.model.ModelFactory;

/**
 * Symbolic execution entry point for TinyBrakeVSUM.
 *
 * Each public method corresponds to one scenario under symbolic exploration.
 * The userInput parameters are left symbolic by the Galette framework —
 * constraint recording happens inside the reactions via getOrMakeSymbolicInt
 * (Galette agent instrumentation) and PathUtils.addIfComparisonConstraint
 * (explicit if-branch constraint recording, mirroring Amalthea's addSwitchConstraint).
 *
 * profileChoice: continuous integer -1 to 100
 *   &lt;  0        → skip    (no AxleControlUnit created)
 *   0  to 33    → off_road (multiplier 0.70)
 *   34 to 66    → comfort  (multiplier 0.85)
 *   67 to 100   → sport    (multiplier 1.20)
 *
 * calibChoice: continuous integer 0 to 100
 *   0  to 32   → conservative (offset -0.5)
 *   33 to 66   → standard     (offset  0.0)
 *   67 to 100  → track        (offset +0.5)
 *   (calibChoice is ignored when profileChoice &lt; 0 / skip, because the match in
 *    applyCalibration silently fails and the dialog is never shown)
 */
public class Test {

    /**
     * Insert a single BrakeDisc with two symbolic continuous user inputs.
     * Covers 4 profile intervals × 3 calibration intervals = up to 10 distinct
     * behavioural paths (the skip path has no calibration branch).
     *
     * @param projectDir    working directory for VSUM storage
     * @param profileChoice symbolic: -1 to 100 (see class javadoc for intervals)
     * @param calibChoice   symbolic:  0 to 100 (see class javadoc for intervals)
     */
    public void insertBrakeDisc(Path projectDir, int profileChoice, int calibChoice) {
        System.out.println("[Test.insertBrakeDisc] profileChoice=" + profileChoice + ", calibChoice=" + calibChoice);

        if (profileChoice < -1 || profileChoice > 100) {
            System.err.println("Invalid profileChoice: " + profileChoice + " (expected -1 to 100)");
            return;
        }
        if (calibChoice < 0 || calibChoice > 100) {
            System.err.println("Invalid calibChoice: " + calibChoice + " (expected 0 to 100)");
            return;
        }

        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        var userInteraction = new TestUserInteraction();
        userInteraction.addNextTextInput(String.valueOf(profileChoice));
        // When profileChoice < 0 (skip), applyCalibration's match block fails and
        // the dialog is never shown, so calibChoice is not consumed.
        if (profileChoice >= 0) {
            userInteraction.addNextTextInput(String.valueOf(calibChoice));
        }

        InternalVirtualModel vsum = new VirtualModelBuilder()
                .withStorageFolder(projectDir)
                .withUserInteractorForResultProvider(new TestUserInteraction.ResultProvider(userInteraction))
                .withChangePropagationSpecifications(new Model2Model2ChangePropagationSpecification())
                .buildAndInitialize();
        vsum.setChangePropagationMode(ChangePropagationMode.TRANSITIVE_CYCLIC);

        addBrakeSystem(vsum, projectDir);
        addBrakeDisc(vsum, 300.0, "steel", 10, 30.0);

        try {
            mergeAndSave(vsum, projectDir.resolve("galette-test-output"), "vsum-output.xmi");
        } catch (IOException e) {
            throw new RuntimeException("Could not persist VSUM result", e);
        }
    }

    /**
     * Insert TWO BrakeDiscs with four independent symbolic continuous user inputs.
     * Used for multi-variable path exploration.
     *
     * @param projectDir     working directory for VSUM storage
     * @param profileChoice1 symbolic profile for first disc  (-1 to 100)
     * @param calibChoice1   symbolic calibration for first disc (0 to 100)
     * @param profileChoice2 symbolic profile for second disc (-1 to 100)
     * @param calibChoice2   symbolic calibration for second disc (0 to 100)
     */
    public void insertTwoDiscs(
            Path projectDir, int profileChoice1, int calibChoice1, int profileChoice2, int calibChoice2) {
        System.out.println("[Test.insertTwoDiscs] disc1=(" + profileChoice1 + "," + calibChoice1 + ") disc2=("
                + profileChoice2 + "," + calibChoice2 + ")");

        if (profileChoice1 < -1 || profileChoice1 > 100 || profileChoice2 < -1 || profileChoice2 > 100) {
            System.err.println("Invalid profileChoice (expected -1 to 100)");
            return;
        }
        if (calibChoice1 < 0 || calibChoice1 > 100 || calibChoice2 < 0 || calibChoice2 > 100) {
            System.err.println("Invalid calibChoice (expected 0 to 100)");
            return;
        }

        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        var userInteraction = new TestUserInteraction();
        userInteraction.addNextTextInput(String.valueOf(profileChoice1));
        if (profileChoice1 >= 0) {
            userInteraction.addNextTextInput(String.valueOf(calibChoice1));
        }
        userInteraction.addNextTextInput(String.valueOf(profileChoice2));
        if (profileChoice2 >= 0) {
            userInteraction.addNextTextInput(String.valueOf(calibChoice2));
        }

        InternalVirtualModel vsum = new VirtualModelBuilder()
                .withStorageFolder(projectDir)
                .withUserInteractorForResultProvider(new TestUserInteraction.ResultProvider(userInteraction))
                .withChangePropagationSpecifications(new Model2Model2ChangePropagationSpecification())
                .buildAndInitialize();
        vsum.setChangePropagationMode(ChangePropagationMode.TRANSITIVE_CYCLIC);

        addBrakeSystem(vsum, projectDir);
        addBrakeDisc(vsum, 300.0, "steel", 10, 30.0); // first disc
        addBrakeDisc(vsum, 350.0, "cast iron", 8, 25.0); // second disc (different diameter → unique tag)

        try {
            mergeAndSave(vsum, projectDir.resolve("galette-test-output"), "vsum-output.xmi");
        } catch (IOException e) {
            throw new RuntimeException("Could not persist VSUM result", e);
        }
    }

    /* -------------------------------------------------- helpers -------------------------------------------------- */

    private void addBrakeSystem(VirtualModel vsum, Path projectDir) {
        CommittableView view = getDefaultView(vsum, List.of(BrakeSystem.class)).withChangeDerivingTrait();
        modifyView(
                view,
                v -> v.registerRoot(
                        ModelFactory.eINSTANCE.createBrakeSystem(),
                        URI.createFileURI(
                                projectDir.resolve("brake_source.model").toString())));
    }

    private void addBrakeDisc(VirtualModel vsum, double diameter, String material, int coolingVanes, double thickness) {
        CommittableView view = getDefaultView(vsum, List.of(BrakeSystem.class)).withChangeDerivingTrait();
        modifyView(view, v -> {
            var disc = ModelFactory.eINSTANCE.createBrakeDisc();
            disc.setDiameter(diameter);
            disc.setMaterial(material);
            disc.setCoolingVanes(coolingVanes);
            disc.setThickness(thickness);
            v.getRootObjects(BrakeSystem.class)
                    .iterator()
                    .next()
                    .getBrakeDiscs()
                    .add(disc);
        });
    }

    private View getDefaultView(VirtualModel vsum, Collection<Class<?>> rootTypes) {
        var selector = vsum.createSelector(ViewTypeFactory.createIdentityMappingViewType("default"));
        selector.getSelectableElements().stream()
                .filter(e -> rootTypes.stream().anyMatch(t -> t.isInstance(e)))
                .forEach(e -> selector.setSelected(e, true));
        return selector.createView();
    }

    private void modifyView(CommittableView view, Consumer<CommittableView> change) {
        change.accept(view);
        view.commitChanges();
    }

    private static void mergeAndSave(InternalVirtualModel vm, Path outDir, String fileName) throws IOException {
        Files.createDirectories(outDir);

        ResourceSet rs = new ResourceSetImpl();
        URI mergedUri = URI.createFileURI(outDir.resolve(fileName).toString());
        Resource merged = rs.createResource(mergedUri);

        for (Resource src : vm.getViewSourceModels()) {
            for (EObject obj : src.getContents()) {
                merged.getContents().add(EcoreUtil.copy(obj));
            }
        }

        Map<String, Object> opts = Map.of(
                XMLResource.OPTION_ENCODING, "UTF-8",
                XMLResource.OPTION_FORMATTED, Boolean.TRUE,
                XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        merged.save(opts);
    }
}
