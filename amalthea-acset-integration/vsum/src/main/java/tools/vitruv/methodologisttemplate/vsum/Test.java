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

    /**
     * Single-variable version: Insert one task with one user choice.
     * Used for single-variable path exploration (5 paths).
     */
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

    /**
     * Multi-variable version: Insert TWO tasks with TWO user choices.
     * Used for multi-variable path exploration (5 × 5 = 25 paths).
     *
     * @param projectDir Working directory for VSUM
     * @param userInput1 First user choice (task type for first task)
     * @param userInput2 Second user choice (task type for second task)
     */
    public void insertTwoTasks(Path projectDir, Integer userInput1, Integer userInput2) {
        // 1) First decision: Task type for first task
        switch (userInput1) {
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

        // 2) Second decision: Task type for second task
        switch (userInput2) {
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

        System.out.println("[Test:insertTwoTasks] CALLED");

        // 3) Setup user interactions for BOTH tasks
        TestUserInteraction userInteraction = new TestUserInteraction();
        userInteraction.addNextSingleSelection(userInput1); // For first task
        userInteraction.addNextSingleSelection(userInput2); // For second task

        // 4) Initialize VSUM
        InternalVirtualModel vsum = new VirtualModelBuilder()
                .withStorageFolder(projectDir)
                .withUserInteractorForResultProvider(new TestUserInteraction.ResultProvider(userInteraction))
                .withChangePropagationSpecifications(new Amalthea2ascetChangePropagationSpecification())
                .buildAndInitialize();

        vsum.setChangePropagationMode(ChangePropagationMode.TRANSITIVE_CYCLIC);

        // 5) Add component container
        addComponentContainer(vsum, projectDir);

        // 6) Add FIRST task (triggers first user interaction)
        addTaskWithName(vsum, "task1");

        // 7) Add SECOND task (triggers second user interaction)
        addTaskWithName(vsum, "task2");

        // 8) Merge and save
        try {
            Path outDir = projectDir.resolve("galette-test-output");
            mergeAndSave(vsum, outDir, "vsum-output.xmi");
        } catch (IOException e) {
            throw new RuntimeException("Could not persist VSUM result", e);
        }
    }

    /* helpers */

    private void addComponentContainer(VirtualModel vsum, Path projectDir) {
        // Ensure the model file path exists as a directory for Vitruvius resource creation
        try {
            java.nio.file.Files.createDirectories(projectDir);
        } catch (java.io.IOException e) {
            // Directory likely already exists, continue
        }

        CommittableView view = getDefaultView(vsum, Collections.singletonList(ComponentContainer.class))
                .withChangeDerivingTrait();
        modifyView(
                view,
                v -> v.registerRoot(
                        Model2Factory.eINSTANCE.createComponentContainer(),
                        URI.createFileURI(projectDir.resolve("example.model").toString())));
    }

    private void addTask(VirtualModel vsum) {
        addTaskWithName(vsum, "specialname");
    }

    private void addTaskWithName(VirtualModel vsum, String taskName) {
        CommittableView view = getDefaultView(vsum, Collections.singletonList(ComponentContainer.class))
                .withChangeDerivingTrait();
        modifyView(view, v -> {
            tools.vitruv.methodologisttemplate.model.model2.Task task = Model2Factory.eINSTANCE.createTask();
            task.setName(taskName);
            v.getRootObjects(ComponentContainer.class)
                    .iterator()
                    .next()
                    .getTasks()
                    .add(task);
        });
    }

    private View getDefaultView(VirtualModel vsum, Collection<Class<?>> rootTypes) {
        tools.vitruv.framework.views.ViewSelector selector =
                vsum.createSelector(ViewTypeFactory.createIdentityMappingViewType("default"));
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

        Map<String, Object> opts = new HashMap<>();
        opts.put(XMLResource.OPTION_ENCODING, "UTF-8");
        opts.put(XMLResource.OPTION_FORMATTED, Boolean.TRUE);
        opts.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        merged.save(opts);
    }
}
