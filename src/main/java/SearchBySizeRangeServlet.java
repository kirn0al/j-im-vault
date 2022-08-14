import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("searchBySizeRange")
public class SearchBySizeRangeServlet extends HttpServlet {

    private final ImageService imageService;

    // TODO: create AmazonS3Service inside ImagesUploaderApplication and inject in current class through constructor (same as ImageService)
    private final AmazonS3Service amazonS3Service = new AmazonS3Service();

    public SearchBySizeRangeServlet(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("searchByImageSizeRange.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Image> imagesList = imageService.getTopBySizeRange(
            Integer.parseInt(req.getParameter("from")),
            Integer.parseInt(req.getParameter("to"))
        );
        List<ImageDto> imageDtoList = new ArrayList<>();
        if (!imagesList.isEmpty()){ // TODO: dont forget about spaces, dont forget about java conventions
            // TODO: Rewrite this loop to streams (I mean stream API)
            for (Image image : imagesList) {
                imageDtoList.add(new ImageDto(
                    image,
                    amazonS3Service.getImageAsBase64String(image.getS3ObjectKey())
                ));
            }
        } else {
            req.setAttribute("notFoundMessage", "Images were not found in this range =(");
            req.setAttribute("notFoundStyle", "style = \"display : none\"");
        }
        req.setAttribute("imageDtosList", imageDtoList); // TODO: rename from imageDtosList to imageDtos
        req.getRequestDispatcher("globalTop.jsp").forward(req, resp);
    }
}
