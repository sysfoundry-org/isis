package demoapp.dom.types.javalang.doubles.jdo;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.runtime.events.app.AppLifecycleEvent;
import org.apache.isis.testing.fixtures.applib.fixturescripts.FixtureScript;
import org.apache.isis.testing.fixtures.applib.fixturescripts.FixtureScripts;

import demoapp.dom.types.Samples;

@Service
public class WrapperDoubleJdoSeedService {

    @EventListener(AppLifecycleEvent.class)
    public void onAppLifecycleEvent(AppLifecycleEvent event) {

        if (event.getEventType() == AppLifecycleEvent.EventType.appPostMetamodel) {
            fixtureScripts.run(new WrapperDoubleJdoEntityFixture());
        }
    }

    @Inject
    FixtureScripts fixtureScripts;

    static class WrapperDoubleJdoEntityFixture extends FixtureScript {

        @Override
        protected void execute(ExecutionContext executionContext) {
            samples.stream()
                    .map(WrapperDoubleJdo::new)
                    .forEach(repositoryService::persist);
        }

        @Inject
        RepositoryService repositoryService;

        @Inject
        Samples<Double> samples;
    }
}