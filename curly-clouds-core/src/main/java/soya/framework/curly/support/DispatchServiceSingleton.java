package soya.framework.curly.support;

import com.google.common.collect.ImmutableSet;
import soya.framework.curly.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class DispatchServiceSingleton implements DispatchService {
    protected static DispatchService instance;

    protected DispatchServiceSingleton() {
    }

    public static DispatchServiceBuilder builder() {
        return new DispatchServiceBuilder();
    }

    public static DispatchService getInstance() {
        return instance;
    }

    public static class DispatchServiceBuilder {
        private DispatchExecutor executor;
        private Set<UriDispatchService> dispatchServices = new HashSet<>();

        private DispatchServiceBuilder() {
        }

        public DispatchServiceBuilder setExecutor(DispatchExecutor executor) {
            this.executor = executor;
            return this;
        }

        public DispatchServiceBuilder register(UriDispatchService dispatchService) {
            dispatchServices.add(dispatchService);
            return this;
        }

        public DispatchService build() {
            return new CompositeDispatchService(executor, dispatchServices);
        }
    }

    static class CompositeDispatchService extends DispatchServiceSingleton implements DispatchRegistration {
        private DispatchExecutor executor;
        private ImmutableSet<UriDispatchService> dispatchServices;
        private Map<String, DispatchService> dispatchServiceMappings = new HashMap<>();

        protected CompositeDispatchService(DispatchExecutor executor, Set<UriDispatchService> dispatchServices) {
            this.executor = executor;
            this.dispatchServices = ImmutableSet.copyOf(dispatchServices);
            instance = this;
        }

        @Override
        public Object dispatch(Object caller, String uri, Object[] args) throws DispatchException {
            DispatchService dispatchService = findDispatchService(uri);
            if (dispatchService != null) {
                try {
                    return doDispatch(caller, uri, args, dispatchService);

                } catch (ExecutionException | InterruptedException e) {
                    throw new ProcessException(e);
                }

            } else {
                throw new DispatchException("Cannot dispatch  '" + uri + "': DispatchService is not found.");

            }
        }

        private DispatchService findDispatchService(String uri) {
            DispatchService dispatchService = null;
            if (dispatchServiceMappings.containsKey(uri)) {
                dispatchService = dispatchServiceMappings.get(uri);

            } else {
                for (UriDispatchService service : dispatchServices) {
                    if (service.match(uri)) {
                        dispatchServiceMappings.put(uri, service);
                        dispatchService = service;
                        break;
                    }
                }
            }

            return dispatchService;
        }

        private Object doDispatch(Object caller, String uri, Object[] args, DispatchService dispatcher) throws ExecutionException, InterruptedException {
            if (executor == null) {
                return dispatcher.dispatch(caller, uri, args);

            } else {
                Future<Object> future = executor.submit(() -> {
                    return dispatcher.dispatch(caller, uri, args);
                });

                while (!future.isDone()) {
                    Thread.sleep(50l);
                }

                return future.get();
            }
        }

        @Override
        public Set<String> schemas() {
            Set<String> set = new LinkedHashSet<>();
            dispatchServices.forEach(e -> {
                set.addAll(e.schemas());
            });

            return set;
        }

        @Override
        public Set<String> available() {
            Set<String> set = new LinkedHashSet<>();
            dispatchServices.forEach(e -> {
                set.addAll(e.available());
            });
            return set;
        }

        @Override
        public boolean contains(String uri) {
            return false;
        }

        @Override
        public DispatchMethod getDispatchMethod(String uri) {
            DispatchRegistration registration = (DispatchRegistration) findDispatchService(uri);
            return registration.getDispatchMethod(uri);
        }

        @Override
        public Operation getProcessor(String uri) {

            DispatchRegistration registration = (DispatchRegistration) findDispatchService(uri);
            return registration.getProcessor(uri);
        }
    }
}
