### Versioning can be achieved in different ways:
* URI Versioning(Ex: domain/v2/users)
* Query string Versioning(Ex: domain/users?version=2)
* Header Versioning(Ex: domain/users, Custom-Header: api-version=2)
* Media type Versioning or Content Negotiation(Ex: domain/users,Accept: application/vnd.unify.v1+json)

### Points to consider
* We are implementing URI versioning because it is a popular way of managing API versions due to its simple implementation and provides default caching.
* Build your services to backward compatible so that you can avoid versioning as much as possible!
* Make Changes Backwards-Compatible and Think about Forwards-Compatibility.

### Guides
The following guides illustrate how to version a API:
* Format of API should follow pattern: `/{API-CONTEXT}/{VERSION}/{API-OPERATION}`
    * For example: /person/v1.0/add
    * VERSION should start with letter **v** followed by version number which has format of **XX.Y**
* As we are building backward compatible API, this does not mean legacy API will be there forever. Hence every old API which is supposed to be scrapped in near future; should be marked as `@Deprecated(since="2.0", forRemoval=true)`. Here value of `since` depends on API change. 
* We are using `notes` section of `@ApiOperation` to specify API version. 
    * Deprecated API will have its expected **Expiry Date** in `notes` section. 
    * Latest API will have a **Latest API** in its `notes` section.
    * Some of the examples:
    
```
@ApiOperation(value = "Save Person", notes = "Latest API", produces = "application/json")
@GetMapping("/person/v2.1/add")
```
```
@ApiOperation(value = "Save Person", notes = "version 2.0", produces = "application/json")
@GetMapping("/person/v2.0/add")
```
```
@ApiOperation(value = "Save Person", notes = "Deprecated API, kept for legacy support, Expiry: 31 Dec 2020", produces = "application/json")
@GetMapping("/person/v1.0/add")
```
    
### Dev Challenges
As we introduced new version of API, we may have a different versions of RequestDto (`@RequestBody`) and ResponseDto(`@ResponseBody`) classes also. 
We can even write whole new different version of `@RestController`. 
So in this case, naming convention for these classes becomes tricky. We are not supposed to name new DTO class like PersonOld or PersonNew.
    
###### Solution
We will follow version based package structure for dto and controller package. 
API versioning should not lead to any other new version of classes apart from dto and controller. 
Service and Repository should be designed for backward compatibility with some deprecated methods for legacy support. 
Only latest API classes(dto and controller) will be in base/versionless package.

For example:
* com.backend.boilerplate.web.controller.v10
* com.backend.boilerplate.web.controller.v20
* com.backend.boilerplate.dto.v10
* com.backend.boilerplate.dto.v20
  
Note:
v21 is latest version, Hence its controllers will remain to stay in com.backend.boilerplate.web.controller package and its dto will remain to stay in com.backend.boilerplate.dto package