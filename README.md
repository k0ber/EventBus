# Event Buses

����� ���� ����� ������������ �������� ���������� �������� ���� �������.
���� ����������� ��� �������:

  - [GreenRobot event bus] 
  - [Otto]
  - RxJava implementation

[GreenRobot event bus]: https://github.com/greenrobot/EventBus
[Otto]: https://github.com/square/otto

## GreenRobor EventBus V3.0.0 (04.02.2016)
Gradle:
```groovy
compile 'org.greenrobot:eventbus:3.0.0'
```
�������� ������:
```java
EventBus.getDefault().post(event);
```
�������� �� ������:
```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(TimeEvent event) {...}
```
� ������� ���� ������������ ��������, ������� ������������ ����� �����������.
����� ����� ���������������� � ������������ ���� �������, ���� �� �����-�� ������� ��������� ��� �� �������������:
```java
EventBus bus = EventBus.builder()
    .eventInheritance(false)
    .throwSubscriberException(true)
    .logNoSubscriberMessages(false)
    .logSubscriberExceptions(true)
    .build();
```

���� �� ��������� �����, � ������, ����� ����� �� �������� �� ����, ������ �� ���������. ������, ����� ������������  **_stycky_** ������ - ����� ������ ����� ���������� �����������, ��� ������ ��� ����������, ���� ���� � ������ �������� ��� �� ���� ��������� �� ��� �������.
�������� �����-������:
```java
EventBus.getDefault().postSticky(new MessageEvent());
```
�������� �� �����-�����:
```java
@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent event) {...}
```
�������� ��������� ��������� ��������� ��� ����������� ��������� �������:
```java
@Subscribe(priority = 1, threadMode = ThreadMode.MAIN)
```
�� ������� ��������� ����� 0. ���� ����������� ��������� � ��� ����� ������ ���������, �� ��� ����� ��������� ��������� � ������� �������� ����������. ��� ����� ��������� ��������, ���� ��� ����� ���������� ������� � ����������� �������.

��� �� ����� ��������������� ������� �����-������
```java
MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
if (stickyEvent != null) {
   EventBus.getDefault().removeStickyEvent(stickyEvent);
}
```
����� ������ removeStickyEvent  ��������� �����-����� ����� ����� � ��������� ��� ������� �� ����������. ��� ����� ������������ � ��������� � ����������� - ��������� � ������� ����� � ���������� � ������� �����������, ��������� ����������, � ������� �����������, ���� ����� ��� �� �������.

�������� ��������� ��������� �����, �� ������� ����� ������ ����� ����������:
```java
@Subscribe(threadMode = ThreadMode.MAIN)
```
������� ���� ����, ����� ������������ ��� ������ � ����
```java
@Subscribe(threadMode = ThreadMode.BACKGROUND)
```
��������� ���� � ����� ���� ������ ����, �� �� ����� �������� ������, � �������, ��������, ���������� ������ � ����� ������, ������, ������� ���� ����� ����������� �� �����.
```java
@Subscribe(threadMode = ThreadMode.ASYNC)
```
����������� ���������� ������, ��������, ��� ����� ����� ������� � ����� ������, ����� ������������ ��� �������� �� ������, ������ �� ����� �������� ����� ������� � ����� **threadMode** ������������, ��� ����� �������� � ��������� � �������������������.

## Otto 1.3.8 (17.01.2015)

Gradle
```groovy
compile 'com.squareup:otto:1.3.8'
```

�������� ������:
```java
bus.post(event);
```
�������� �� ������:
```java
@Subscribe
public void onTimeEvent(TimeEvent event) {...}
```
��� �������� ���� ���� ����� �������� � ����������� **ThreadEnforcer** - 
**ThreadEnforcer.MAIN** ����� ���������, ��� ����� ���� ������ � ���� ������, ���� ��������, ��� ��� �� ���, �� ������� �������
_Event bus [Bus "default"] accessed from non-main thread Looper_
**ThreadEnforcer.ANY** �� ��������� �����.

������ ����� ������� � ���� ���� ��������� **@Produce**
```java
    @Produce
    public MessageEvent onMessageAvailable() {
        return stickyEventForOtto;
    }
```
������, ������� ��������� ����� ������ ���� ��������������� ����� �� �������, ��� � �����������. �����, ���������� ���������� ������� ����� ������ ���� ���, ��� ������� ����������, ������� ���������� �� ������. ����� �������� ����� ���������, _otto_ ������ ��� �����, ������� �� ������� � ���� ������� ������. ��������������, �������� ����� ����� �����, ������� �����:
```java
    public void cancelStickyEvents() {
        stickyEventForOtto = null;
    }
```
����� ��� ��������, ���� ����� ��� ��� ������ �������� ���������� � ������ �� ���������. ������� ������ ���������� ���������� ������ ���� ��� ��� ��������, �������, ���� �� ������, ����� ��� ����� ����������� ��� � ������ ��� ��� ������������� �������, �� ��������� ���������� ��� ������� **post**���. �������, ��� � _otto_ ����� ����� ������ ������ ���������� �� ����� �����.

## RxBus
���������� Otto ��������� ��� deprecated, � ���� ����, ��� � ��� ���� rxJava, ������� ��������� �������������� ���� ������� ��� ������ �������, ��� ���� ������������ ���� ������������, ��������, ���������� �������� � ������������ ���������.

���������� ���������� ���� �� rxJava ����� ��������� ���:
```java
public class RxBus {
   private final Subject<Object, Object> bus;
   public RxBus() {
       bus = new SerializedSubject<>(PublishSubject.create());
   }
   public Observable<Object> getObservable() {
       return bus;
   }
   public void post(Object o) {
       bus.onNext(o);
   }
}
```
�������� ������:
```java
bus.post(new TimeEvent());
```
�������� �� ������:
```java
bus.getObservable().subscribe((this::onEvent));
```
```java
private void onEvent(Object event) {
   if (event instanceof TimeEvent) {
       onTimeEvent(((TimeEvent) event));
   }
}
```
���� ������������ rxBus ��� �������� ������-�� ������ ���� �������, ����� ������� ��� ����� � ���������, ����� �������� �������� **instanceof**.
"Sticky" ������ ����������� �� ���������� �����, ���������� ���� ������� **BehaviorSubject**, ������, ����� ����� ������� ��������� ��������, ������� ������� ����������� ��� ��������, ����� ��� ������� ������� ���������� �� ����. � ���������� ������ ��������� ���-�� �����:
```java
stickyBus = new SerializedSubject<>(BehaviorSubject.create());
```
```java
public void postSticky(Object o) {
    stickyBus.onNext(o);
}

public void removeStickyEvent() {
    stickyBus.onNext(null);
}
```
������ ������������ ����� �������� ��������� ������������ ����� ��� ������ ���������� ��������. ��� �� � ����� ������ ����� ������� ����� �� �������.

## ������
�� ��� ������, ����� ���������� �� ���� ������������ otto � ����� ��������, �������� ����� ������ ������� ���� [otto plugin], �������, ��� �� ����� ����� �� ���������� � �������� �� ��������� ������� AndroidStudio �����������. ������� ���������� �������� event bus �� green robot, ������� ������������� ����� ������� ���������� ��� ��� �� �������� ������. ������ ���� ��������� ����� ��������� (green robot v.3.0.0 ����� ~50k), � ������� �� rxJava (v1.1.9 ����� 1.1M), �������, ���� � ����� ������� �� ������������ ��� ����������, �� ������ �����, ��� ��� ������ ������������� rxBus. ���� �� ������ ������� � rxJava �� ����������� ���� ����������� ���� ������� �� �������� �����, ������  �� ����� �� ������� ��������� � ������������ ���� ������������ ����, ���� ����� ������ ���� ����������� ������������ Observable ����� ������ ����������� ������, � ���������� ���������� ����� ���� ��� ������������� ����.
[otto plugin]: https://github.com/square/otto-intellij-plugin