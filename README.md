
# central-reference-data-eis-stub

This service stubs the EIS (Enterprise Integration Services) endpoints consumed by the Central Reference Data Inbound Orchestrator service. It is responsible to simulate the responses of EIS services for local development and testing


## Development Setup
- Run locally: `sbt run` which runs on port `7251` by default

## Configuration

Bearer tokens are configured in `application.conf` and must be set per environment via `app-config-xxx`:

| Token | Config Key |
|-------|-----------|
| CSRD120 extract bearer token | `tokens.extract-bearer-token` |
| CSRD130 subscription bearer token | `tokens.subscription-bearer-token` |

## API

All routes are prefixed by `/central-reference-data-eis-stub`.

| Path | Supported Methods | Description |
|------|-------------------|-------------|
| `/csrd/referencedataupdate/v1` | POST | Simulates EIS CSRD120 endpoint (reference data extract). |
| `/csrd/eudeltaextractreference/v1` | POST | Simulates EIS CSRD130 endpoint (subscription message). |

### Required Headers

All endpoints require the following headers:

| Header | Value |
|--------|-------|
| `Authorization` | `Bearer <token>` |
| `Accept` | `application/xml` |
| `Content-Type` | `application/xml; charset=UTF-8` (CSRD120) or `application/xml` (CSRD130) |
| `X-Forwarded-Host` | Any value |
| `X-Correlation-Id` | Any value |
| `Date` | Any value |

Missing or invalid headers return `400 Bad Request`. An invalid bearer token returns `401 Unauthorized`.

### Scalafmt

Check all project files are formatted as expected as follows:

> `sbt scalafmtCheckAll`

Format `*.sbt` and `project/*.scala` files as follows:

> `sbt scalafmtSbt`

Format all project files as follows:

> `sbt scalafmtAll`

### Tests

Run all unit tests with command:

> `sbt test`

Run all integration tests command:

> `sbt it/test`

Run Unit and Integration Tests command:

>  `sbt test it/test`


### All tests and checks
This is an sbt command alias specific to this project. It will run a scala format
check, run unit tests, run integration tests and produce a coverage report:
> `sbt runAllChecks`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").