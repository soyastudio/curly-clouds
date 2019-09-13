package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.curly.*;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class DispatchServiceSingleton implements DispatchService {
    protected static DispatchService instance;

    protected DispatchServiceSingleton() {
    }

    public static DispatchService getInstance() {
        return instance;
    }
    public static DispatchServiceBuilder builder() {
        return new DispatchServiceBuilder();
    }

    public static class DispatchServiceBuilder {
        private DispatchExecutor executor;
        private Set<DispatchServiceSupport> dispatchServices = new HashSet<>();

        private DispatchServiceBuilder() {
        }

        public DispatchServiceBuilder setExecutor(DispatchExecutor executor) {
            this.executor = executor;
            return this;
        }

        public DispatchServiceBuilder register(DispatchServiceSupport dispatchService) {
            dispatchServices.add(dispatchService);
            return this;
        }

        public DispatchService build() {
            return new CompositeDispatchService(executor, dispatchServices);
        }
    }

    static class CompositeDispatchService extends DispatchServiceSingleton implements DispatchRegistration, DispatchServiceComposite {
        private DispatchExecutor executor;
        private ImmutableMap<String, DispatchServiceSupport> dispatchServices;
        private Map<String, DispatchService> dispatchServiceMappings = new HashMap<>();

        protected CompositeDispatchService(DispatchExecutor executor, Set<DispatchServiceSupport> dispatchServices) {
            this.executor = executor;
            ImmutableMap.Builder<String, DispatchServiceSupport> builder = ImmutableMap.builder();
            dispatchServices.forEach(d -> {
                builder.put(d.getName(), d);
            });
            this.dispatchServices = builder.build();
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
                for (DispatchServiceSupport service : dispatchServices.values()) {
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

        public String[] getDispatchServices() {
            List<String> list = new ArrayList<>(dispatchServices.keySet());
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }

        @Override
        public DispatchRegistration getDispatchService(String name) {
            return dispatchServices.get(name);
        }

        @Override
        public String getName() {
            return "Composite Dispatch Service";
        }

        @Override
        public Set<String> schemas() {
            Set<String> set = new LinkedHashSet<>();
            dispatchServices.values().forEach(e -> {
                set.addAll(e.schemas());
            });

            return set;
        }

        @Override
        public Set<String> available() {
            Set<String> set = new LinkedHashSet<>();
            dispatchServices.values().forEach(e -> {
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
