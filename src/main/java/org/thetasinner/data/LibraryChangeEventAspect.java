package org.thetasinner.data;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thetasinner.web.events.ChangeEventData;
import org.thetasinner.web.events.LibraryChangeService;

import static org.thetasinner.web.events.ChangeEventData.ChangeType.BookDeleted;

@Aspect
@Component
public class LibraryChangeEventAspect {
  private final LibraryChangeService libraryChangeService;

  @Autowired
  public LibraryChangeEventAspect(LibraryChangeService changeService) {
    this.libraryChangeService = changeService;
  }

  @Around("@annotation(EmitDeleteEvent)")
  public Object handleBookDeleted(ProceedingJoinPoint joinPoint) throws Throwable {
    var id = joinPoint.getArgs()[0];
    var name = joinPoint.getArgs()[1];

    var eventData = new ChangeEventData(BookDeleted, (String) id);
    libraryChangeService.publish((String) name, eventData);

    return joinPoint.proceed();
  }
}
