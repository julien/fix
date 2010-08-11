import java.io.*;

public class Fix {      

    
    private static String AIR_CONFIG_FILE =            "air-config.xml";	
	private static String AIR_DESCRIPTOR_FILE =        "air-descriptor.xml";
    private static String ANT_BUILD_FILE =             "build.xml";
    private static String BUILD_VERSION =              "0.1";
    private static String DEFAULT_BIN_DIRECTORY_NAME = "bin-debug";
    private static String DEFAULT_LIB_DIRECTORY_NAME = "libs";
    private static String DEFAULT_PROJECT_NAME =       "project";
    private static String DEFAULT_SRC_DIRECTORY_NAME = "src";
    private static String FLEX_CONFIG_FILE =           "flex-config.xml";
    private static String MANIFEST_FILE =              "manifest.xml";
        
    public static void main(String[] args) {
    	
    	if(args.length < 1) {
    		printHelp();
    		System.exit(0);
    	}
    	
    	String cmd = args[0];
    	int length = args.length - 1;
    	String[] cmdArgs = new String[length];
    	System.arraycopy(args, 1, cmdArgs, 0, length);

    	boolean hasArgs = cmdArgs.length > 0; 	
    	
    	String pathname = null;
    	
        if(cmd.equalsIgnoreCase("create")) {
    		if(hasArgs) 
    			pathname = cmdArgs[0];
    		else
    			pathname = DEFAULT_PROJECT_NAME;	
    		createProject(pathname);
    	}
    	else if(cmd.equalsIgnoreCase("delete")) {
    		if(hasArgs) {
    			pathname = cmdArgs[0];
    			deleteProject(pathname);
    		}    		
    	}
        else if(cmd.equalsIgnoreCase("generate")) {

            if(!hasArgs || cmdArgs.length < 1) {
                System.out.println("Usage : ");
                System.out.println("java Fix generate type ( air | ant | descriptor | flex | manifest ) ");
                System.out.println("\t" + "Generates a file from a template");     
                System.exit(0);
            }
            
            String type = cmdArgs[0];
            if(type.equalsIgnoreCase("air")) {
                pathname = AIR_CONFIG_FILE;
            }
            else if(type.equalsIgnoreCase("ant")) {
                pathname = ANT_BUILD_FILE;
            }
            else if(type.equalsIgnoreCase("descriptor")) {
                pathname = AIR_DESCRIPTOR_FILE;
            }
            else if(type.equalsIgnoreCase("flex")) {
                pathname = FLEX_CONFIG_FILE;
            }
            else if(type.equalsIgnoreCase("manifest")) {
                pathname = MANIFEST_FILE;
            }
            else {
                System.out.println("Unknown template : " + type);
                printHelp();
                System.exit(0);
            }

            if(pathname != null)
                generateFile(pathname, pathname);

        }
    	else if(cmd.equalsIgnoreCase("help")) {
    		printHelp();
    	}
    	else {
    		System.out.println("Unknown command : " + cmd);
    		printHelp();
    	}
    }
    
    public static File createDirectory(String pathname)  {
    	File dir = new File(pathname);
    	try {
    		if(!dir.exists()) {
    			if(dir.mkdir()) {
    				System.out.println("Created : " + dir.getCanonicalPath());
    			}
    		} 
    		else if(dir.exists()) {
    			System.out.println("Found : " + dir.getCanonicalPath());
    			if(dir.isFile()) {
    				System.out.println("WARNING : Found a file having the same file name :" + 
    						dir.getCanonicalPath());
    			}
    			if(dir.mkdir()) {
    				System.out.println("Created : " + dir.getCanonicalPath());
    			}
    		}
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return dir;
    }

    public static void createProject(String pathname) {
    	
    	if(pathname == null) 
    		pathname = DEFAULT_PROJECT_NAME;
    	
    	String sep = System.getProperty("file.separator");
        File baseDir = new File(System.getProperty("user.dir"));
        try {
        	System.out.println("Analyzing " + baseDir.getCanonicalPath());
            
            File projectDir = createDirectory(pathname);
            if(projectDir.exists()) {
            	createDirectory(pathname + sep + DEFAULT_BIN_DIRECTORY_NAME);
            	createDirectory(pathname + sep + DEFAULT_LIB_DIRECTORY_NAME);
            	createDirectory(pathname + sep + DEFAULT_SRC_DIRECTORY_NAME);
            	
            	System.out.println(sep + "Project located in " + projectDir.getCanonicalPath() + sep);
            }  
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File dir) {
    	if (dir.isDirectory()) {
    		String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
            	boolean success = deleteDirectory(new File(dir, children[i]));
            	if (!success) {
            		return false;
                }
            }
    	}
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    public static void deleteProject(String pathname) {
    	File dir = new File(pathname);
    	if(!dir.exists()) {
    		System.out.println("No project found in : " + dir.getAbsolutePath());
    	}
    	else {
    		if(deleteDirectory(dir)) {
    			System.out.println("Project deleted");
    		}
    		else {
    			System.out.println("Unable to delete project : " + dir.getAbsolutePath());
    		}
    			
    	}
    }
    
    public static File generateFile(String resource, String pathname) {
    	
    	File outputFile = null;
    	try {
    		File baseDir = new File(System.getProperty("user.dir"));
    		String sep = System.getProperty("file.separator");
    		outputFile = new File(baseDir + sep + pathname);
    		    		
    		InputStream inputStream = Fix.class.getClass().getResourceAsStream(sep + resource);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
    		
    		String line = null;
    		while( ( line = reader.readLine() ) != null ) {
    			writer.write(line);
    			writer.newLine();
    		}
    		reader.close();
    		writer.close();
    		System.out.println("File generated in : " + outputFile.getCanonicalPath());
    	}
    	catch(Exception ex) {
    		System.out.println("Couldn't create file");
    	}
    	
    	return outputFile;
    }
    
    public static void printHelp() {	
        String sep = System.getProperty("line.separator");
    	StringBuffer sb = new StringBuffer();
    	sb.append("=============================================================").append(sep);
    	sb.append("Fix version ").append(BUILD_VERSION).append(" ~ ").append(sep);
    	sb.append("=============================================================").append(sep);
    	sb.append("Help :").append(sep);
        sb.append("java Fix command options").append(sep);
    	sb.append("create name").append(sep).append("\t").append("Creates a project.").append(sep);
    	sb.append("delete name").append(sep).append("\t").append("Deletes a project.").append(sep);
        sb.append("generate type ( air | ant | descriptor | flex | manifest )").append(sep).
            append("\t").append("Generates a file from a template.").append(sep);
        sb.append("help").append(sep).append("\t").append("Prints this message.").append(sep);
    	sb.append(sep);
    	
    	System.out.println(sb.toString());
    }
    

}
