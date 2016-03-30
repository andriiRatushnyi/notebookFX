package baobab.notebookfx.repositories;

import baobab.notebookfx.models.Image;
import java.util.List;

public interface ImageRepositoryCustom {

    void cleanUpImage();
    
    List<Image> getUnrelationImages();
}
