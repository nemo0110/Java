/*
The main method it's the entry point for the application and will subsequently invoke all the other methods required by the program.
It can contain code to execute or call other methods, and it can be placed in any class that’s part of a program. 
The class that contains the main method can have any name.
More complex programs usually have a class that contains only the main method.

The public static void main(String[] args) signature is the mandatory, unchangeable entry point the Java Virtual Machine (JVM) 
requires to execute a standalone application.

*public*
The access modifier of the main method needs to be public so that the JRE(Java Runtime Environment) 
[or the JVM?] can access and execute this method.

*static*
When the Java program starts, there is no object of the class present.
The main method has to be static so that the JVM can load the class into memory and call the main method without creating an instance of the class first.
The modifiers public and static can be written in either order (public static or static public), but the convention is to use public static.

*void*
Every Java method must provide the return type.The Java main method return type is void because it doesn’t return anything.
When the main method is finished executing, the Java program terminates, so there is no need for a returned object.

*main*
The Java main method is always named main. When a Java program starts, it always looks for the main method. 
While mandatory for standalone programs, the main method is not the entry point in container-managed environments like web or Android applications.

*String[] args*
Java main method accepts a single argument of type String array.
Each string in the array is called a command line argument.
The String array argument can be written as String... args or String args[].
You can use command line arguments to affect the operation of the program, or to pass information to the program, at runtime.
To prevent common runtime errors, always validate the number of arguments using args.length before accessing array elements.



*/

public class Main {
    public static void main(String[] args) {
    }
}

/*
Missing the public Modifier: 
If you omit the public access modifier, the main method is no longer visible to the JVM from outside the class. 
The public modifier makes a method part of a class’s public interface, accessible by any external entity.
Since the JVM is an external process that initiates your code, it needs public access to find and invoke the main method.
Without it, the method is hidden from the JVM’s scope, and you will typically receive an error indicating the main method was not found or is not public.

Missing the static Modifier: 
Omitting the static keyword makes the main method an instance method, which requires an object to be called upon.
The JVM’s execution process begins by loading your class into memory, but it does not create an object of your class at startup.
A non-static (instance) method can only be invoked on an object.
Since no object exists, the JVM has no way to call a non-static main method.
This is why the entry point must be static; it belongs to the class itself and can be called without an instance.

Changing the void Return Type:
In Java, a method’s signature includes its name and parameter types, but the return type is also part of the contract for invocation.
The JVM is hard-coded to look for an entry point that returns void.
By changing the return type from void to int, you have defined a completely different method.
From the JVM’s perspective, the required main method that returns void is now missing entirely.
Your code may compile, but the JVM will not find the specific signature it needs to start the program.

Altering the Method Name or Parameter Type:
Any change to the method’s name (e.g., Main) or its parameter type (e.g., String instead of String[]) also breaks the signature contract.
The JVM is programmed to look for a method named exactly main (case-sensitive) that accepts exactly one parameter of type String[].
Any deviation creates a method that, while valid in other contexts, does not match the required entry point signature.
The JVM will conclude that no main method exists and will terminate with an error.

*Best Practices for the main Method*

Keep the main Method Lean: 
Treat the main method as a coordinator, not a worker. 
Its sole responsibility should be to parse any command-line arguments and delegate the actual work by creating objects and calling their methods.
Avoid placing complex business logic inside main, as this improves readability, maintainability, and makes your code easier to test.

Handle Command-Line Arguments Gracefully: 
Always validate command-line arguments before using them.
Check args.length to ensure the user has provided the required input, and use try-catch blocks when parsing numbers.
This prevents common runtime errors like ArrayIndexOutOfBoundsException and NumberFormatException and makes your application more robust and user-friendly.

Use System.exit() for Clear Termination Status:
Signal your program’s final outcome to the operating system using System.exit().
By convention, exit with System.exit(0) for a successful run and a non-zero number (like System.exit(1)) to indicate that an error occurred.
This is crucial for scripting and automation, as it allows other tools to know if your program ran successfully.

Use a Dedicated Entry-Point Class:
For small, single-file programs, placing the main method in your primary class is fine.
However, in larger applications, it’s a best practice to put the main method in its own dedicated class (e.g., Application.java or Main.java). 
This clearly separates the program’s starting point from its core logic, improving overall organization.

*/

