package com.example.resource;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;

import com.example.model.Person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path( "/people" ) 
@Tag(name = "people")
public class PeopleRestService {
    private Map<String, Person> people = new ConcurrentHashMap<>();
    
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Operation(
        description = "List all people", 
        responses = {
            @ApiResponse(
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Person.class))),
                responseCode = "200"
            )
        }
    )
    public Collection<Person> getPeople() {
        return people.values();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{email}")
    @GET
    @Operation(
        description = "Find person by e-mail", 
        responses = {
            @ApiResponse(
                content = @Content(schema = @Schema(implementation = Person.class)), 
                responseCode = "200"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Person with such e-mail doesn't exists"
            )
        }
    )
    public Person findPerson(@Parameter(description = "E-Mail address to lookup for", required = true) @PathParam("email") final String email) {
        final Person person = people.get(email);
        
        if (person == null) {
            throw new NotFoundException("Person with such e-mail doesn't exists");
        }
        
        return person;
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Operation(
        description = "Create new person",
        responses = {
            @ApiResponse(
                 content = @Content(
                      schema = @Schema(implementation = Person.class), 
                      mediaType = MediaType.APPLICATION_JSON
                 ),
                 headers = @Header(name = "Location"),
                 responseCode = "201"
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Person with such e-mail already exists"
            )
        }
    )
    public Response addPerson(@Context final UriInfo uriInfo,
            @Parameter(description = "E-Mail", required = true) @FormParam("email") final String email, 
            @Parameter(description = "First Name", required = true) @FormParam("firstName") final String firstName, 
            @Parameter(description = "Last Name", required = true) @FormParam("lastName") final String lastName) {
        
        final Person person = people.get(email);
        
        if (person != null) {
            return Response
                .status(Status.CONFLICT)
                .entity("Person with such e-mail already exists")
                .build();
        }
        
        people.put(email, new Person(email, firstName, lastName));
        return Response
             .created(uriInfo.getRequestUriBuilder().path(email).build())
             .build();
    }
    
    @Path("/{email}")
    @DELETE
    @Operation(
        description = "Delete existing person",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Person has been deleted"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Person with such e-mail doesn't exists"
            )
        }
    )
    public Response deletePerson(@Parameter(description = "E-Mail address to lookup for", required = true ) @PathParam("email") final String email) {
        if (people.remove(email) == null) {
            throw new NotFoundException("Person with such e-mail doesn't exists");
        }
        return Response.noContent().build();
    }
}
