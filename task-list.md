

- [ ] Refactor the commanbd and query handler interfaces, so that the contracts only reference classes inside the domain.  This means sending the Command and Query classes intot eh domain, and out of the application layer

- [ ] Implement a new Global Exception Handler that Intercepts validation errors for the Command and for the Query modules


- [ ] Implement a better versioning strategy for th docker and helm packages; when it's main we use the release version, but we don't have  stratecgy for other branches