/* File  : FileUploader.java
 * Author: Try Ajitiono
 */

package newsclassifier;

import java.io.File;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.*;

/**
 *
 * @author ASUS
 */
public class FileUploader {
    public void uploadFile(HttpServletRequest request) throws Exception {
        String path = new File("").getAbsolutePath();
        // Create a new file upload handler 
        DiskFileUpload upload = new DiskFileUpload();
        // parse request
        List items = upload.parseRequest(request);

        // get uploaded file 
        FileItem  file = (FileItem) items.get(0);

        // get target filename
        FileItem  name = (FileItem) items.get(1);
        String  target = name.getString();
        
        // create file, file path ../glassfish folder
        File outfile = new File("../../../../" +  target);
        file.write(outfile);

        System.out.println("Upload Is Successful!");
    }
}
