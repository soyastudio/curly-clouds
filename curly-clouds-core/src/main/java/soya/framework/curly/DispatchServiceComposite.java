package soya.framework.curly;

public interface DispatchServiceComposite {
    String[] getDispatchServices();

    DispatchRegistration getDispatchService(String name);
}
