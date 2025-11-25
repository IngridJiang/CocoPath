package tools.vitruv.methodologisttemplate.vsum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import mir.reactions.amalthea2ascet.Amalthea2ascetChangePropagationSpecification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.testutils.TestUserInteraction;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;
import tools.vitruv.methodologisttemplate.model.model2.ComponentContainer;
import tools.vitruv.methodologisttemplate.model.model2.Model2Factory;

public class Test {

    public void insertTask(Path projectDir, Integer userInput) {
        // 1)
        // Execute switch with tagged value to collect path constraints
        switch (userInput) {
            case 0:
                break; // InterruptTask
            case 1:
                break; // PeriodicTask
            case 2:
                break; // SoftwareTask
            case 3:
                break; // TimeTableTask
            case 4:
                break; // Decide later
            default:
                break;
        }

        TestUserInteraction userInteraction = new TestUserInteraction();
        userInteraction.addNextSingleSelection(userInput);

        // 2)  VSUM
        InternalVirtualModel vsum = new VirtualModelBuilder()
                .withStorageFolder(projectDir)
                .withUserInteractorForResultProvider(new TestUserInteraction.ResultProvider(userInteraction))
                .withChangePropagationSpecifications(new Amalthea2ascetChangePropagationSpecification())
                .buildAndInitialize();

        vsum.setChangePropagationMode(ChangePropagationMode.TRANSITIVE_CYCLIC);

        // 3) VSUM
        addComponentContainer(vsum, projectDir);
        addTask(vsum);

        // 4) merge and save
        try {
            Path outDir = projectDir.resolve("galette-test-output");
            mergeAndSave(vsum, outDir, "vsum-output.xmi");
        } catch (IOException e) {
            throw new RuntimeException("Could not persist VSUM result", e);
        }
    }

    /* ------------------------------------------------- helpers ------------------------------------------------- */

    private void addComponentContainer(VirtualModel vsum, Path projectDir) {
        CommittableView view = getDefaultView(vsum, Collections.singletonList(ComponentContainer.class))
                .withChangeDerivingTrait();
        modifyView(
                view,
                v -> v.registerRoot(
                        Model2Factory.eINSTANCE.createComponentContainer(),
                        URI.createFileURI(projectDir.resolve("example.model").toString())));
    }

    private void addTask(VirtualModel vsum) {
        CommittableView view = getDefaultView(vsum, Collections.singletonList(ComponentContainer.class))
                .withChangeDerivingTrait();
        modifyView(view, v -> {
            tools.vitruv.methodologisttemplate.model.model2.Task task = Model2Factory.eINSTANCE.createTask();
            task.setName("specialname");
            v.getRootObjects(ComponentContainer.class)
                    .iterator()
                    .next()
                    .getTasks()
                    .add(task);
        });
    }

    /** */
    private View getDefaultView(VirtualModel vsum, Collection<Class<?>> rootTypes) {
        tools.vitruv.framework.views.ViewSelector selector =
                vsum.createSelector(ViewTypeFactory.createIdentityMappingViewType("default"));
        selector.getSelectableElements().stream()
                .filter(e -> rootTypes.stream().anyMatch(t -> t.isInstance(e)))
                .forEach(e -> selector.setSelected(e, true));
        return selector.createView();
    }

    /** */
    private void modifyView(CommittableView view, Consumer<CommittableView> change) {
        change.accept(view);
        view.commitChanges();
    }

    /** */
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

        Map<String, Object> opts = new HashMap<>();
        opts.put(XMLResource.OPTION_ENCODING, "UTF-8");
        opts.put(XMLResource.OPTION_FORMATTED, Boolean.TRUE);
        opts.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        merged.save(opts);
    }
}
