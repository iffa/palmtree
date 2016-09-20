package xyz.santeri.palmtree.data.remote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;
import xyz.santeri.palmtree.data.model.ListingType;

/**
 * @author Santeri Elo
 */
public class JsoupListingService implements ListingService {
    private static final String FRONTPAGE_URL = "http://naamapalmu.com/listing/%s";
    private static final String LATEST_URL = "http://naamapalmu.com/filelist/images/latest/listing/%s";

    @Override
    public Observable<ImageDetails> getListing(ListingType type, int pageNumber) {
        return Observable.create(subscriber -> {
            Document doc = null;

            try {
                switch (type) {
                    case FRONT_PAGE:
                        doc = Jsoup.connect(String.format(FRONTPAGE_URL, pageNumber)).get();
                        break;
                    case LATEST_IMAGES:
                        doc = Jsoup.connect(String.format(LATEST_URL, pageNumber)).get();
                        break;
                    default:
                        doc = Jsoup.connect(String.format(FRONTPAGE_URL, pageNumber)).get();
                        break;
                }
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (doc == null || doc.select("div.file") == null) {
                    subscriber.onError(new NullPointerException("Document is invalid"));
                }
            }

            assert doc != null;
            Elements files = doc.select("div.file");

            for (Element file : files) {
                int id;
                boolean nsfw = false;
                ImageType imageType = ImageType.IMAGE;
                String fileUrl = null;
                String title = null;
                String rating = null;

                title = file.select("p.filetitle").first().text();

                if (file.select("p.nsfwwarning").size() > 0) {
                    nsfw = true;
                }

                if (file.select("a > img").size() > 0) {
                    fileUrl = file.select("a > img").first().attr("src");
                    imageType = ImageType.IMAGE;
                } else if (file.select("video > source").size() > 0) {
                    fileUrl = file.select("video > source").first().attr("src");
                    imageType = ImageType.VIDEO;
                }

                id = Integer.parseInt(file.select("p.filetitle > a").first().attr("href").split("/")[4]);

                rating = file.select("div.listingcomments > span").first().text();

                ImageDetails imageDetails = ImageDetails.create(id, fileUrl, imageType, nsfw, title, rating);
                subscriber.onNext(imageDetails);
            }

            subscriber.onCompleted();
        });
    }
}
