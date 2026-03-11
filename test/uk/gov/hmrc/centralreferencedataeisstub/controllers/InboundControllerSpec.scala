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

import org.apache.pekko.stream.Materializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http
import play.api.http.HeaderNames
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.centralreferencedataeisstub.config.AppConfig

class InboundControllerSpec extends AnyWordSpec, GuiceOneAppPerSuite, Matchers:

  private val fakeRequest = FakeRequest("GET", "/")
  private val appConfig   = app.injector.instanceOf[AppConfig]
  private val controller  = new InboundController(appConfig, Helpers.stubControllerComponents())
  given mat: Materializer = app.injector.instanceOf[Materializer]

  private val validTestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780912</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  private val invalid402TestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780402</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  private val invalid404TestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780404</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  private val invalid503TestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780503</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  private val invalid504TestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780504</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <IncludedBinaryObject>c04a1612-705d-4373-8840-9d137b14b30a</IncludedBinaryObject>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  private val invalidTestBody: scala.xml.Elem = <MainMessage>
    <Body>
      <TaskIdentifier>780912</TaskIdentifier>
      <AttributeName>ReferenceData</AttributeName>
      <MessageType>gZip</MessageType>
      <File>c04a1612-705d-4373-8840-9d137b14b30a</File>
      <MessageSender>CS/RD2</MessageSender>
    </Body>
  </MainMessage>

  // Subscription message test bodies
  private val validSubscriptionBody: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2"
    xmlns:rdentityentrylisttype="http://xmlns.ec.eu/BusinessObjects/CSRD2/RDEntityEntryListType/V3">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-001</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T10:30:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_UK</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_01</messageheadertype:ReceiverID>
      </MessageHeader>
      <RDEntityList>
        <rdentityentrylisttype:RDEntity>
          <rdentityentrylisttype:EntityID>GB</rdentityentrylisttype:EntityID>
          <rdentityentrylisttype:EntityType>CountryCode</rdentityentrylisttype:EntityType>
          <rdentityentrylisttype:EntityName>United Kingdom</rdentityentrylisttype:EntityName>
          <rdentityentrylisttype:Status>ACTIVE</rdentityentrylisttype:Status>
          <rdentityentrylisttype:EffectiveDate>2024-01-01</rdentityentrylisttype:EffectiveDate>
        </rdentityentrylisttype:RDEntity>
      </RDEntityList>
    </HMRCReceiveReferenceDataReqMsg>

  private val validSubscriptionErrorBody: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-003</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T12:00:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_FR</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_03</messageheadertype:ReceiverID>
      </MessageHeader>
      <ErrorReport>eSBmb3JtYXQu</ErrorReport>
    </HMRCReceiveReferenceDataReqMsg>

  private val subscription402Body: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2"
    xmlns:rdentityentrylisttype="http://xmlns.ec.eu/BusinessObjects/CSRD2/RDEntityEntryListType/V3">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-402</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T10:30:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_UK</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_01</messageheadertype:ReceiverID>
      </MessageHeader>
      <RDEntityList>
        <rdentityentrylisttype:RDEntity>
          <rdentityentrylisttype:EntityID>GB</rdentityentrylisttype:EntityID>
          <rdentityentrylisttype:EntityType>CountryCode</rdentityentrylisttype:EntityType>
          <rdentityentrylisttype:EntityName>United Kingdom</rdentityentrylisttype:EntityName>
          <rdentityentrylisttype:Status>ACTIVE</rdentityentrylisttype:Status>
        </rdentityentrylisttype:RDEntity>
      </RDEntityList>
    </HMRCReceiveReferenceDataReqMsg>

  private val subscription404Body: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2"
    xmlns:rdentityentrylisttype="http://xmlns.ec.eu/BusinessObjects/CSRD2/RDEntityEntryListType/V3">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-404</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T10:30:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_UK</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_01</messageheadertype:ReceiverID>
      </MessageHeader>
      <RDEntityList>
        <rdentityentrylisttype:RDEntity>
          <rdentityentrylisttype:EntityID>GB</rdentityentrylisttype:EntityID>
          <rdentityentrylisttype:EntityType>CountryCode</rdentityentrylisttype:EntityType>
          <rdentityentrylisttype:EntityName>United Kingdom</rdentityentrylisttype:EntityName>
          <rdentityentrylisttype:Status>ACTIVE</rdentityentrylisttype:Status>
        </rdentityentrylisttype:RDEntity>
      </RDEntityList>
    </HMRCReceiveReferenceDataReqMsg>

  private val subscription503Body: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2"
    xmlns:rdentityentrylisttype="http://xmlns.ec.eu/BusinessObjects/CSRD2/RDEntityEntryListType/V3">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-503</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T10:30:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_UK</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_01</messageheadertype:ReceiverID>
      </MessageHeader>
      <RDEntityList>
        <rdentityentrylisttype:RDEntity>
          <rdentityentrylisttype:EntityID>GB</rdentityentrylisttype:EntityID>
          <rdentityentrylisttype:EntityType>CountryCode</rdentityentrylisttype:EntityType>
          <rdentityentrylisttype:EntityName>United Kingdom</rdentityentrylisttype:EntityName>
          <rdentityentrylisttype:Status>ACTIVE</rdentityentrylisttype:Status>
        </rdentityentrylisttype:RDEntity>
      </RDEntityList>
    </HMRCReceiveReferenceDataReqMsg>

  private val subscription504Body: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4"
    xmlns:messageheadertype="http://xmlns.ec.eu/BusinessObjects/CSRD2/MessageHeaderType/V2"
    xmlns:rdentityentrylisttype="http://xmlns.ec.eu/BusinessObjects/CSRD2/RDEntityEntryListType/V3">
      <MessageHeader>
        <messageheadertype:MessageID>MSG-2024-12-30-504</messageheadertype:MessageID>
        <messageheadertype:MessageDateTime>2024-12-30T10:30:00Z</messageheadertype:MessageDateTime>
        <messageheadertype:SenderID>CUSTOMS_AUTHORITY_UK</messageheadertype:SenderID>
        <messageheadertype:ReceiverID>SUBSCRIBER_SYSTEM_01</messageheadertype:ReceiverID>
      </MessageHeader>
      <RDEntityList>
        <rdentityentrylisttype:RDEntity>
          <rdentityentrylisttype:EntityID>GB</rdentityentrylisttype:EntityID>
          <rdentityentrylisttype:EntityType>CountryCode</rdentityentrylisttype:EntityType>
          <rdentityentrylisttype:EntityName>United Kingdom</rdentityentrylisttype:EntityName>
          <rdentityentrylisttype:Status>ACTIVE</rdentityentrylisttype:Status>
        </rdentityentrylisttype:RDEntity>
      </RDEntityList>
    </HMRCReceiveReferenceDataReqMsg>

  private val invalidSubscriptionBody: scala.xml.Elem =
    <HMRCReceiveReferenceDataReqMsg
    xmlns="http://xmlns.ec.eu/BusinessObjects/CSRD2/ReferenceDataSubscriptionReceiverCBSServiceType/V4">
      <InvalidElement>This should fail validation</InvalidElement>
    </HMRCReceiveReferenceDataReqMsg>

  "POST /" should {
    "accept a valid message" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe ACCEPTED
    }

    "return bad request if accept header is not present" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if accept header is not application/xml" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/text",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if content type header is not present" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if content type header is not application/xml;charset=UTF-8" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/text",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if x-forwarded-host header is not present" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}",
            "X-Correlation-Id"        -> "some-correlation-id",
            HeaderNames.DATE          -> "some-date"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if x-correlation-id header is not present" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if date header is not present" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return accepted if all required headers are present and valid" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe ACCEPTED
    }

    "return bad request if the xml body provided does not match the schema" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.bearerToken}"
          )
          .withBody(invalidTestBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return null response if the task id ends with 402 to simulate this return code" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(invalid402TestBody)
      )
      status(result) shouldBe PAYMENT_REQUIRED
    }

    "return not found if the task id ends with 404 to simulate this return code" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(invalid404TestBody)
      )
      status(result) shouldBe NOT_FOUND
    }

    "return service unavailable if the task id ends with 503 to simulate this return code" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(invalid503TestBody)
      )
      status(result) shouldBe SERVICE_UNAVAILABLE
    }

    "return gateway timeout if the task id ends with 504 to simulate this return code" in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.bearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(invalid504TestBody)
      )
      status(result) shouldBe GATEWAY_TIMEOUT
    }

    "return unauthorized if bearer token is not present " in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT       -> "application/xml",
            HeaderNames.CONTENT_TYPE -> "application/xml; charset=UTF-8"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe UNAUTHORIZED
    }

    "return unauthorized if bearer token is incorrect " in {
      val result = controller.submit()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> "incorrect"
          )
          .withBody(validTestBody)
      )
      status(result) shouldBe UNAUTHORIZED
    }
  }

  "POST /submitSubscriptionMessage" should {
    "accept a valid subscription message with RDEntityList" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe ACCEPTED
    }

    "accept a valid subscription message with ErrorReport" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validSubscriptionErrorBody)
      )
      status(result) shouldBe ACCEPTED
    }

    "return bad request if accept header is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.subscriptionBearerToken}"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if accept header is not application/xml" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/text",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.subscriptionBearerToken}"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if content type header is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.subscriptionBearerToken}"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if content type header is not application/xml;charset=UTF-8" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/text",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.subscriptionBearerToken}"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if x-forwarded-host header is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.subscriptionBearerToken}",
            "X-Correlation-Id"        -> "some-correlation-id",
            HeaderNames.DATE          -> "some-date"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if x-correlation-id header is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if date header is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return bad request if the xml body does not match the subscription schema" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(invalidSubscriptionBody)
      )
      status(result) shouldBe BAD_REQUEST
    }

    "return payment required if the MessageID ends with 402 to simulate this return code" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(subscription402Body)
      )
      status(result) shouldBe PAYMENT_REQUIRED
    }

    "return not found if the MessageID ends with 404 to simulate this return code" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(subscription404Body)
      )
      status(result) shouldBe NOT_FOUND
    }

    "return service unavailable if the MessageID ends with 503 to simulate this return code" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(subscription503Body)
      )
      status(result) shouldBe SERVICE_UNAVAILABLE
    }

    "return gateway timeout if the MessageID ends with 504 to simulate this return code" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT           -> "application/xml",
            HeaderNames.CONTENT_TYPE     -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION    -> s"Bearer ${appConfig.subscriptionBearerToken}",
            HeaderNames.X_FORWARDED_HOST -> "some-host",
            "X-Correlation-Id"           -> "some-correlation-id",
            HeaderNames.DATE             -> "some-date"
          )
          .withBody(subscription504Body)
      )
      status(result) shouldBe GATEWAY_TIMEOUT
    }

    "return unauthorized if bearer token is not present" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT       -> "application/xml",
            HeaderNames.CONTENT_TYPE -> "application/xml; charset=UTF-8"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe UNAUTHORIZED
    }

    "return unauthorized if bearer token is incorrect" in {
      val result = controller.submitSubscriptionMessage()(
        fakeRequest
          .withHeaders(
            HeaderNames.ACCEPT        -> "application/xml",
            HeaderNames.CONTENT_TYPE  -> "application/xml; charset=UTF-8",
            HeaderNames.AUTHORIZATION -> "incorrect"
          )
          .withBody(validSubscriptionBody)
      )
      status(result) shouldBe UNAUTHORIZED
    }
  }
