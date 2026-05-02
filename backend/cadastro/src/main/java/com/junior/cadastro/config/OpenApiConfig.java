package com.junior.cadastro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.junior.cadastro.exceptions.ApiError;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Cadastro API",
        version = "v1",
        description = """
                API para autenticação, gestão de usuários e integração com Pluggy.

                Principais códigos de erro:
                - 400: JSON inválido, parâmetro inválido ou erro de banco/regra de requisição
                - 401: usuário não autenticado ou credenciais inválidas
                - 403: usuário autenticado sem permissão
                - 404: recurso não encontrado
                - 409: e-mail já cadastrado
                - 422: erro de validação nos campos enviados
                - 500: erro interno inesperado
                - 502: erro de integração com a Pluggy
                """,
        contact = @Contact(
            name = "José Junior",
            url = "https://github.com/josejunior30"
        ),
        license = @License(
            name = "Uso interno"
        )
    )
)
public class OpenApiConfig {

	@Bean
	OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .addServersItem(new Server()
	                    .url("http://localhost:8080")
	                    .description("Ambiente local"))
	            .components(new Components()
	                    .addSecuritySchemes("bearerAuth", bearerAuthScheme())

	                    .addSchemas("ApiError", new Schema<ApiError>()
	                            .name("ApiError")
	                            .description("Resposta padrão de erro da API"))

	                    .addResponses("BadRequest", errorResponse(
	                            "Requisição inválida",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 400,
	                              "error": "JSON inválido",
	                              "message": "O corpo da requisição está inválido ou malformado.",
	                              "path": "/user"
	                            }
	                            """
	                    ))

	                    .addResponses("Unauthorized", errorResponse(
	                            "Não autenticado ou credenciais inválidas",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 401,
	                              "error": "Não autenticado",
	                              "message": "Token ausente, inválido ou expirado",
	                              "path": "/pluggy/accounts"
	                            }
	                            """
	                    ))

	                    .addResponses("Forbidden", errorResponse(
	                            "Acesso negado",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 403,
	                              "error": "Acesso negado",
	                              "message": "Você não tem permissão para acessar este recurso",
	                              "path": "/user"
	                            }
	                            """
	                    ))

	                    .addResponses("NotFound", errorResponse(
	                            "Recurso não encontrado",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 404,
	                              "error": "Recurso não encontrado",
	                              "message": "Usuário não encontrado. Id: 99",
	                              "path": "/user/99"
	                            }
	                            """
	                    ))

	                    .addResponses("Conflict", errorResponse(
	                            "Conflito de dados",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 409,
	                              "error": "Email já cadastrado",
	                              "message": "Já existe um usuário cadastrado com este email.",
	                              "path": "/user"
	                            }
	                            """
	                    ))

	                    .addResponses("UnprocessableEntity", errorResponse(
	                            "Erro de validação",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 422,
	                              "error": "Erro de validação",
	                              "message": "email: email obrigatório",
	                              "path": "/auth/login"
	                            }
	                            """
	                    ))

	                    .addResponses("BadGateway", errorResponse(
	                            "Erro na integração com a Pluggy",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 502,
	                              "error": "Erro na integração com a Pluggy",
	                              "message": "Pluggy indisponível ao buscar contas.",
	                              "path": "/pluggy/items/sync"
	                            }
	                            """
	                    ))

	                    .addResponses("InternalServerError", errorResponse(
	                            "Erro interno inesperado",
	                            """
	                            {
	                              "timestamp": "2026-05-02T12:00:00Z",
	                              "status": 500,
	                              "error": "Erro interno",
	                              "message": "Ocorreu um erro inesperado no servidor.",
	                              "path": "/user"
	                            }
	                            """
	                    )));
	}
	
    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }

    private ApiResponse errorResponse(String description, String exampleJson) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ApiError"))
                                .example(exampleJson)
                                .addExamples("default", new Example().value(exampleJson))
                ));
    }
}