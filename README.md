# Event Buses

Перед вами обзор существующих подходов реализации паттерна шина событий.
Было рассмотрено три решения:

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
Отправка ивента:
```java
EventBus.getDefault().post(event);
```
Подписка на ивенты:
```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(TimeEvent event) {...}
```
В примере выше используется синглтон, который предоставлен самой библиотекой.
Также можно сконфигурировать и использовать свой инстанс, если по какой-то причине дефолтный вас не удовлетворяет:
```java
EventBus bus = EventBus.builder()
    .eventInheritance(false)
    .throwSubscriberException(true)
    .logNoSubscriberMessages(false)
    .logSubscriberExceptions(true)
    .build();
```

Если вы отправите ивент, в момент, когда никто не подписан на него, ничего не произойдёт. Однако, можно использовать  **_stycky_** ивенты - такие ивенты будут доставлены подписчикам, как только они подпишутся, даже если в момент отправки они не были подписаны на это событие.
Отправка стики-ивента:
```java
EventBus.getDefault().postSticky(new MessageEvent());
```
Подписка на стики-ивент:
```java
@Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent event) {...}
```
ИвентБас позволяет указывать приоритет для подписчиков следующим образом:
```java
@Subscribe(priority = 1, threadMode = ThreadMode.MAIN)
```
по дефолту приоритет равен 0. если подписчиков несколько и они имеют разный приоритет, то они будут синхронно вызыванны в порядке убывания приоритета. Это может оказаться полезным, если вам важно обработать событие в определённом порядке.

Так же можно воспользоваться отменой стики-ивента
```java
MessageEvent stickyEvent = EventBus.getDefault().getStickyEvent(MessageEvent.class);
if (stickyEvent != null) {
   EventBus.getDefault().removeStickyEvent(stickyEvent);
}
```
после вызова removeStickyEvent  указанный стики-ивент будет удалён и подписчик его никогда не обработает. Это можно использовать в сочитании с приоритетом - обработав и отменив ивент в подписчике с высоким приоритетом, остальные подписчики, с меньшим приоритетом, этот ивент уже не получат.

ИвентБас позволяет указывать поток, на котором будет вызван метод подписчика:
```java
@Subscribe(threadMode = ThreadMode.MAIN)
```
андроид мэйн тред, можно использовать для работы с юаем
```java
@Subscribe(threadMode = ThreadMode.BACKGROUND)
```
бэкграунд тред в ивент басе только один, на нём можно вызывать методы, в которых, например, происходит работа с базой данных, однако, надолго этот поток блокировать не стоит.
```java
@Subscribe(threadMode = ThreadMode.ASYNC)
```
асинхронное выполнение метода, означает, что метод будет запущен в новом потоке, можно использовать для запросов на сервер, однако не стоит вызывать много методов с таким **threadMode** одновременно, это может привести к проблемам с производительностью.

## Otto 1.3.8 (17.01.2015)

Gradle
```groovy
compile 'com.squareup:otto:1.3.8'
```

Отправка ивента:
```java
bus.post(event);
```
Подписка на ивенты:
```java
@Subscribe
public void onTimeEvent(TimeEvent event) {...}
```
При создании отто шины можно передать в конструктор **ThreadEnforcer** - 
**ThreadEnforcer.MAIN** будет проверять, что метод пост вызван в мэйн потоке, если окажется, что это не так, он выкинет эксепшн
_Event bus [Bus "default"] accessed from non-main thread Looper_
**ThreadEnforcer.ANY** не проверяет поток.

Вместо стики ивентов в отто есть аннотация **@Produce**
```java
    @Produce
    public MessageEvent onMessageAvailable() {
        return stickyEventForOtto;
    }
```
Объект, который продъюсит ивент должен быть зарегистрирован таким же образом, как и сабскрайбер. Метод, помеченный аннотацией продъюс будет вызван один раз, для каждого подписчика, который подпишется на ивенты. Когда появится новый подписчик, _otto_ отдаст ему ивент, который вы вернули в этом продъюс методе. Соответственно, отменить такой ивент можно, занулив ивент:
```java
    public void cancelStickyEvents() {
        stickyEventForOtto = null;
    }
```
Тогда при подписке, отто решит что ему нечего отдавать подписчику и ничего не произойдёт. Продъюс ивенты передаются подписчику только один раз при подписке, поэтому, если вы хотите, чтобы ваш ивент передавался ещё и каждый раз при возникновении собития, не забывайте отправлять его обычным **post**’ом. Помните, что в _otto_ можно иметь только одного продъюсера на каждй ивент.

## RxBus
Библиотека Otto объявлена как deprecated, в виду того, что у нас есть rxJava, которая позволяет заимплементить шину событий без особых проблем, при этом предоставляя кучу возможностей, например, управление потоками и всевозможные операторы.

Простейшая реализация шины на rxJava может выглядеть так:
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
Отправка ивента:
```java
bus.post(new TimeEvent());
```
Подписка на ивенты:
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
Если использовать rxBus для передачи какого-то одного типа ивентов, можно указать его класс в обсёрвабле, чтобы избежать проверки **instanceof**.
"Sticky" ивенты реализовать не составляет труда, достаточно лишь создать **BehaviorSubject**, причём, можно сразу указать дефолтное значение, которое получит сабскрайбер при подписке, когда ещё никаких ивентов отправлено не было. В простейшем случае получится что-то вроде:
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
Теперь сабскрайберы будут получить последний отправленный ивент как только происходит подписка. Так же в любой момент можно удалить ивент из очереди.

## Выводы
На мой взгляд, стоит отказаться от идеи использовать otto в новых проектах, выделяет среди других решений лишь [otto plugin], который, тем не менее давно не обновлялся и работает на последних версиях AndroidStudio нестабильно. Гораздо интереснее выглядит event bus от green robot, который предоставляет более богатый функционал при той же простоте работы. Размер этих библиотек очень маленький (green robot v.3.0.0 весит ~50k), в отличае от rxJava (v1.1.9 весит 1.1M), поэтому, если в вашем проекте не используется эта библиотека, то скорее всего, вам нет смысла реализовывать rxBus. Если вы хорошо дружите с rxJava то реализовать свою собственную шину событий не составит труда, однако  не стоит всё слишком усложнять и использовать одну навороченную шину, ведь почти всегда есть возможность использовать Observable прямо внутри конкретного класса, и уведомлять слушателей через него без использования шины.
[otto plugin]: https://github.com/square/otto-intellij-plugin