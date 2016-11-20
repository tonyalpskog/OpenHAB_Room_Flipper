package org.openhab.habclient.dagger;

import org.openhab.habclient.rest.RestModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AndroidApplicationModule.class,
        EventBusModule.class,
        ApplicationModule.class,
        UtilModule.class,
        WidgetModule.class,
        CommandModule.class,
        NotificationModule.class,
        WearModule.class,
        RestModule.class
})
@Singleton
public interface AppComponent {
    RuleEditActivityComponent ruleEditActivity();

    UnitContainerComponent unitContainerView();

    GraphicUnitComponent graphicUnit();

    RuleActionComponent ruleAction();

    RuleOperationComponent ruleOperation();

    RuleListComponent ruleList();

    OperatorSelectionComponent operatorSelection();

    UnitOperandSelectionComponent unitOperandSelection();

    RuleOperandComponent ruleOperand();

    MainComponent main();

    UnitPlacementComponent unitPlacement();

    RoomFlipperComponent roomFlipper();

    WidgetListActivityComponent widgetListActivity();

    SpeechComponent speech();

    RoomConfigActivityComponent roomConfigActivity();

    WearListenerServiceComponent wearListenerService();

    MainActivityComponent mainActivity();

    WidgetListFragmentComponent widgetListFragment();

    RoomConfigFragmentComponent roomConfigFragment();

    InfoActivityComponent infoActivity();
}
