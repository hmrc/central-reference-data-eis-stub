/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.centralreferencedataeisstub.config.AppConfig

import java.io.StringReader
import javax.inject.{Inject, Singleton}
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import scala.concurrent.Future
import scala.util.{Success, Try}
import scala.xml.NodeSeq

@Singleton
class InboundController @Inject()(appConfig:AppConfig, cc: ControllerComponents)
  extends BackendController(cc):

  private val RequiredAccept = "application/xml"
  private val RequiredContentType = "application/xml; charset=UTF-8"

  def submit(): Action[NodeSeq] = Action.async(parse.xml) { implicit request =>
    Future.successful(
      if validateBearerToken(request.headers) then
        if validateHeaders(request.headers) && validateRequestBody(request.body) then
          expectedReturnValue(request.body)
        else
          // this line handles the 400 bad request stub
          BadRequest
      else
        Unauthorized
    )
  }

  private def validateHeaders(headers: Headers): Boolean =
    (headers.get(ACCEPT), headers.get(CONTENT_TYPE), headers.get(X_FORWARDED_HOST), headers.get("X-Correlation-Id"), headers.get(DATE)) match
      case (Some(RequiredAccept), Some(RequiredContentType), Some(_), Some(_), Some(_)) => true
      case a@_ => false

  private def validateBearerToken(headers:Headers): Boolean = 
    headers.get(AUTHORIZATION).getOrElse(UNAUTHORIZED) == appConfig.bearerToken

  private def validateRequestBody(body: NodeSeq) =
    Try {
      val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      val xsd = getClass.getResourceAsStream("/schemas/csrd120main-v1.xsd")
      val schema = factory.newSchema(new StreamSource(xsd))
      val validator = schema.newValidator()
      validator.validate(new StreamSource(new StringReader(body.toString)))
    } match {
      case Success(_) => true
      case _ => false
    }

  private def expectedReturnValue(body: NodeSeq): Result =
    (body \\ "TaskIdentifier").text match {
      case nullResponse if nullResponse.endsWith("402") => PaymentRequired
      case notFound if notFound.endsWith("404") => NotFound
      case serviceUnavailable if serviceUnavailable.endsWith("503") => ServiceUnavailable
      case gatewayTimeout if gatewayTimeout.endsWith("504") => GatewayTimeout
      case _ => Accepted
    }

