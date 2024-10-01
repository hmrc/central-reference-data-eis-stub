/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.centralreferencedataeisstub.controllers

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.libs.ws.DefaultBodyWritables.*
import uk.gov.hmrc.centralreferencedataeisstub.config.AppConfig

class InboundControllerISpec
  extends AnyWordSpec,
    Matchers,
    ScalaFutures,
    IntegrationPatience,
    GuiceOneServerPerSuite:

  private val wsClient = app.injector.instanceOf[WSClient]
  private val appConfig = app.injector.instanceOf[AppConfig]
  private val baseUrl = s"http://localhost:$port"

  private val validTestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780912</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  "EIS CSRD120 POST / endpoint" should {
    "respond with 202 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/central-reference-data-eis-stub/services/crdl/referencedataupdate/v1")
          .withHttpHeaders(
            HeaderNames.ACCEPT -> "application/xml",
            HeaderNames.CONTENT_TYPE -> "application/xml;charset=UTF-8",
            HeaderNames.AUTHORIZATION -> appConfig.bearerToken,
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id" -> "some-correlation-id",
            HeaderNames.DATE -> "some-date"
          )
          .post(validTestBody.toString)
          .futureValue

      response.status shouldBe 202
    }
  }



