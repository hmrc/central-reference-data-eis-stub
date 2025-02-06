
# central-reference-data-eis-stub

The Central Reference Data Inbound Orchestrator responsibilities:
- Simulate the responses of EIS services

## Development Setup
- Run locally: `sbt run` which runs on port `7251` by default

## Tests
- Run Unit Tests: `sbt test`
- Run Integration Tests: `sbt it/test`
- Run Unit and Integration Tests: `sbt test it/test`

## API

| Path - internal routes prefixed by `/central-reference-data-eis-stub` | Supported Methods | Type     | Description                                                                |
|----------------------------------------------------------------------|-------------------|----------|----------------------------------------------------------------------------|
| `/csrd/referencedataupdate/v1`                              | POST              | Internal | Simulates EIS CSRD120 endpoint. |

### All tests and checks
This is an sbt command alias specific to this project. It will run a scala format
check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").