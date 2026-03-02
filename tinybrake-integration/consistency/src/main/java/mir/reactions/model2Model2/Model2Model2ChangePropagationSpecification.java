package mir.reactions.model2Model2;

import java.util.Set;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

@SuppressWarnings("all")
public class Model2Model2ChangePropagationSpecification extends AbstractReactionsChangePropagationSpecification
        implements ChangePropagationSpecification {
    public Model2Model2ChangePropagationSpecification() {
        super(
                MetamodelDescriptor.with(Set.of("http://vitruv.tools/methodologisttemplate/model")),
                MetamodelDescriptor.with(Set.of("http://vitruv.tools/methodologisttemplate/model2")));
    }

    protected RoutinesFacadesProvider createRoutinesFacadesProvider(final ReactionExecutionState executionState) {
        return new mir.routines.model2Model2.Model2Model2RoutinesFacadesProvider(executionState);
    }

    protected void setup() {
        org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.putIfAbsent(
                tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.eNS_URI,
                tools.vitruv.methodologisttemplate.model.model.impl.ModelPackageImpl.eINSTANCE);
        org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.putIfAbsent(
                tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl.eNS_URI,
                tools.vitruv.methodologisttemplate.model.model2.impl.Model2PackageImpl.eINSTANCE);
        addReaction(new mir.reactions.model2Model2.BrakeSystemInsertedAsRootReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeDiscInsertedIntoSystemReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeDiscDiameterChangedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeDiscMaterialChangedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeDiscDeletedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakePistonInsertedIntoCaliperReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakePistonDeletedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeCaliperInsertedIntoSystemReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
        addReaction(new mir.reactions.model2Model2.BrakeCaliperDeletedReaction(
                (executionState) -> createRoutinesFacadesProvider(executionState)
                        .getRoutinesFacade(ReactionsImportPath.fromPathString("model2Model2"))));
    }
}
