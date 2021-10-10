package br.com.til.todotasks.controllers.exception.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class ErrorModelBuild {
		
		private ErroModel erroModel = new ErroModel();
		
		public void reset() {
			this.erroModel = new ErroModel();
		}
		
		public ErrorModelBuild withStackTrace(Exception ex) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			this.erroModel.setStackTrace(sw.toString().substring(0, 400));
			return this;
		}

		public ErrorModelBuild withDateTime(LocalDateTime dateTime) {
			this.erroModel.setDateTime(dateTime);
			return this;
		}


		public ErrorModelBuild withErrorMessage(String message) {
			this.erroModel.setErrorMessage(message);
			return this;
		}
		
		public ErrorModelBuild withStatusCode(Integer statusCode) {
			this.erroModel.setStatusCode(statusCode);
			return this;
		}
		
		public ErrorModelBuild withError(String error) {
			this.erroModel.setError(error);
			return this;
		}
		
		public ErroModel getErroModel() {
			return this.erroModel;
		}
	}