import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class ImagesUploaderApplication {

    private static final String WEBAPP_DIR_LOCATION = "src/main/webapp/";
    private static final int TOMCAT_PORT = 8082;

    public static void main(String[] args) throws LifecycleException {
        final DBConnector dbConnector = new DBConnector();
        final ImageRepositoryDB imageRepository = new ImageRepositoryDB(dbConnector.get());
        final ImageService imageService = new ImageService(imageRepository);

        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);

        final Context context = tomcat.addWebapp("", new File(WEBAPP_DIR_LOCATION).getAbsolutePath());
        context.setAllowCasualMultipartParsing(true);

        tomcat.addServlet(context.getPath(), "AddingImage", AddingImageServlet.class.getCanonicalName());
        context.addServletMappingDecoded("/imagesUpload", "AddingImage");

        tomcat.addServlet(context.getPath(), "DisplayImages", DisplayImagesServlet.class.getCanonicalName());
        context.addServletMappingDecoded("/imagesPreview", "DisplayImages");

        tomcat.start();
        tomcat.getServer().await();
    }
}
