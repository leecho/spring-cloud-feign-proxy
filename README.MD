## Introduction
This project can automatically expose FeignClients to Rest without MVC controller.
Use CGLIB dynamic proxy service interface to RestController.

## Feature
- Auto export feign without MVC controller
- Can specify the package path for scanning.
- Support Swagger
  
## Issues
- @PathVariable @HeaderValue @CookieValue must use attr "name" for special
- The real implementor of service interface must use @Primary to avoid bean conflicts