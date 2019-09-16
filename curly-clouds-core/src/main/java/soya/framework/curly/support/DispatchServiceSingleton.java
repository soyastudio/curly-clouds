package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.curly.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class DispatchServiceSingleton {
    protected static DispatchServiceSingleton instance;

    protected DispatchServiceSingleton() {
    }

    public abstract Object dispatch(String uri, Object[] args, Object caller) throws DispatchException;

    public static DispatchServiceSingleton getInstance() {
        return instance;
    }

    public static DispatchServiceBuilder builder() {
        if (instance != null) {
            throw new IllegalArgumentException("The instance of dispatchServiceSingleton has already created");
        }

        return new DispatchServiceBuilder();
    }

    public static class DispatchServiceBuilder {

        private DispatchExecutor executor;
        private SessionDeserializer deserializer;
        private Set<DispatchServiceSupport> dispatchServices = new HashSet<>();
        private Set<SubjectRegistration> subjectRegistrations = new HashSet<>();

        private DispatchServiceBuilder() {
        }

        public DispatchServiceBuilder setExecutor(DispatchExecutor executor) {
            this.executor = executor;
            return this;
        }

        public DispatchServiceBuilder setDeserializer(SessionDeserializer deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public DispatchServiceBuilder registerDispatchService(DispatchServiceSupport dispatchService) {
            dispatchServices.add(dispatchService);
            return this;
        }

        public DispatchServiceBuilder registerSubject(SubjectRegistration registration) {
            subjectRegistrations.add(registration);
            return this;
        }

        public void build() {
            instance = new CompositeDispatchService(dispatchServices, subjectRegistrations, executor, deserializer);
        }
    }

    static class CompositeDispatchService extends DispatchServiceSingleton implements Registration {
        private DispatchExecutor executor;
        private SessionDeserializer deserializer;

        private final SubjectRegistration[] subjectRegistrations;
        private final ImmutableMap<String, DispatchServiceSupport> dispatchServices;
        private final ImmutableMap<String, DispatchMethod> dispatchMethods;

        private Map<String, DispatchServiceSupport> dispatchServiceMappings = new HashMap<>();

        protected CompositeDispatchService(Set<DispatchServiceSupport> dispatchServices,
                                           Set<SubjectRegistration> subjectRegistrations, DispatchExecutor executor, SessionDeserializer deserializer) {
            this.executor = executor;
            this.deserializer = deserializer;
            this.subjectRegistrations = subjectRegistrations.toArray(new SubjectRegistration[subjectRegistrations.size()]);

            ImmutableMap.Builder<String, DispatchServiceSupport> builder = ImmutableMap.builder();
            dispatchServices.forEach(d -> {
                for (String schema : d.schemas()) {
                    builder.put(schema, d);
                }
            });
            this.dispatchServices = builder.build();

            ImmutableMap.Builder<String, DispatchMethod> methodBuilder = ImmutableMap.builder();
            for (SubjectRegistration subjectRegistration : subjectRegistrations) {
                for (DispatchMethod dispatchMethod : subjectRegistration.dispatchMethods()) {
                    methodBuilder.put(dispatchMethod.getUri(), dispatchMethod);
                }
            }
            this.dispatchMethods = methodBuilder.build();
        }

        @Override
        public SubjectRegistration[] subjectRegistrations() {
            return subjectRegistrations;
        }

        @Override
        public DispatchRegistration[] dispatchRegistrations() {
            return dispatchServices.values().toArray(new DispatchRegistration[dispatchServices.size()]);
        }

        @Override
        public DispatchMethod getDispatchMethod(String uri) {
            return dispatchMethods.get(uri);
        }

        @Override
        public String[] schemas() {
            List<String> list = new ArrayList<>(dispatchServices.keySet());
            Collections.sort(list);
            return list.toArray(new String[list.size()]);
        }

        @Override
        public DispatchService getDispatchService(String uri) {
            if (dispatchServiceMappings.containsKey(uri)) {
                return dispatchServiceMappings.get(uri);
            } else {
                int index = uri.indexOf("://");
                if (index < 0) {
                    throw new IllegalArgumentException("Cannot parse uri: " + uri);
                }

                String schema = uri.substring(0, index + 3);
                if (!dispatchServices.containsKey(schema)) {
                    throw new IllegalArgumentException("Dispatch service is not registered for " + schema);
                }

                DispatchServiceSupport dispatchService = dispatchServices.get(schema);
                dispatchServiceMappings.put(uri, dispatchService);

                return dispatchService;
            }
        }

        @Override
        public SessionDeserializer getDeserializer() {
            return deserializer;
        }

        @Override
        public Object dispatch(String uri, Object[] args, Object caller) throws DispatchException {
            DispatchMethod dispatchMethod = getDispatchMethod(uri);
            Method method = dispatchMethod.getMethod();
            Dispatch dispatch = method.getAnnotation(Dispatch.class);

            Operation operation = null;
            if (dispatch != null) {
                String duri = dispatch.uri();
                DispatchService dispatchService = this.getDispatchService(duri);
                operation = dispatchService.create(duri);
            } else {
                operation = getDispatchService(uri).create(uri);
            }

            Session session = new DefaultSession(dispatchMethod.createInvocation(caller, args));
            if (executor != null && operation instanceof CallableOperation) {
                CallableOperation callableOperation = (CallableOperation) operation;
                Future<Session> future = callableOperation.call(session, executor);
                while (future.isDone()) {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    session = future.get();

                } catch (InterruptedException | ExecutionException e) {
                    throw new DispatchException(e);

                }
            } else {
                operation.process(session);
            }

            return deserializer.deserialize(session);
        }
    }
}
