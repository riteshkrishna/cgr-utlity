package org.liverpool.utils;

public class GetCommandLineParameters {
	
	/**
     * Returns the value of a command-line parameter
     * 
     * @param args : command-line arguments (in the form "-argname" "argvalue" )
     * @param name : the expected parameter name 
     * @return returns null if the parameter is not found (and is not required), otherwise throws an error.
     */
    public static String getCmdParameter(String[] args, String name, boolean required) 
    {
            for (int i = 0; i < args.length; i++)
            {
                    String argName = args[i];
                    if (argName.equals("-" + name))
                    {
                            String argValue = "";
                            if (i + 1 < args.length)
                                    argValue = args[i+1];
                            if (required && (argValue.trim().length() == 0 || argValue.startsWith("-")))
                            {
                                    System.err.println("Parameter value expected for " + argName);
                                    throw new RuntimeException("Expected parameter value not found: " + argName);
                            }
                            else if (argValue.trim().length() == 0 || argValue.startsWith("-"))
                                    return "";
                            else
                                    return argValue;
                    }       
            }
            //Nothing found, if required, throw error, else return "";
            if (required)
            {
                    System.err.println("Parameter -" + name + " expected ");
                    throw new RuntimeException("Expected parameter not found: " + name);
            }
            
            return null;
    }

}
