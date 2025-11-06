# Spring Boot Project Notes

## 1. What is Spring Boot?
Spring Boot is a framework for building Java-based applications quickly.  
It simplifies the setup of Spring applications by providing:
- Auto-configuration
- Embedded server (like Tomcat)
- Pre-configured libraries

---

## 2. Project Setup
To create a Spring Boot project for this CRUD API:

1. Go to [Spring Initializr](https://start.spring.io/).
2. Configure your project metadata:

| Field          | Example Value                  | Explanation |
|----------------|--------------------------------|-------------|
| Project        | Maven Project                  | Build tool and dependency manager |
| Language       | Java                           | Programming language |
| Spring Boot    | Latest stable (e.g., 3.x)      | Framework version |
| Group          | com.letsplay                   | Base Java package namespace |
| Artifact       | lets-play                      | Project name / jar name |
| Name           | lets-play                      | Friendly project name |
| Description    | CRUD API project with MongoDB  | Short description |
| Package name   | com.letsplay                   | Base package for your Java code (no `-`) |
| Packaging      | Jar                            | Makes a runnable jar file |
| Java           | 17                             | Java version to use |

3. Add dependencies:
- **Spring Web** → To create RESTful APIs.
- **Spring Data MongoDB** → To connect with MongoDB.
- **Spring Security** → For authentication & role-based authorization.
- **Lombok (optional)** → Reduces boilerplate code.
- **Spring Boot DevTools (optional)** → For hot reload during development.

4. Generate and download the project.
5. Open the project in your IDE (IntelliJ, VSCode, Eclipse).

---

## 3. Project Structure
After opening the project, your folder structure should look like this:

