# UMS
*User Management System*

## Project Overview
*Sumerge, 2019, Software Engineer Talent Program*

This is a project to practise backend web development using Java EE. 

This is user a management system that consists of groups and users. Users can be either normal users or admin users. All users should be logged in before doing any action. All passwords are hashed.

A normal user can do the following:
1) Edit their own information but they can’t change their groups.
2) View other users’ non-sensitive data.
3) Reset their password.

An admin user can do the following:
1) Move users from one group to another.
2) Delete/add new user (Soft deletion).
3) View any user’s data.
4) View all users even the deleted ones.

The system initially has:
1) A default admin that is NOT delete-able nor Editable.
2) A default group that is NOT delete-able.
3) The default admin in the default group and cannot be moved out from it.


The system has an audit log that tracks all user actions and it has the following:
1) New entity state as a json object.
2) Action name.
3) Action time.
4) Action author.

All Exceptions are handled and the system has integration tests to test the rest endpoints.

## Libraries

* Maven: project build
* JAX-RS: web app and RESTful services
* JAAS: authentication and authorization
* JPA: database management (MySQL)
* JTA: transactions
* Log4j: logging
* Gson: json serialization
