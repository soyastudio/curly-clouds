package soya.framework.curly;

public interface Registration {

    SubjectRegistration[] subjectRegistrations();

    DispatchRegistration[] dispatchRegistrations();

    String[] schemas();

    DispatchMethod getDispatchMethod(String uri);

    DispatchService getDispatchService(String uri);

    SessionDeserializer getDeserializer();
}
