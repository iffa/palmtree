package xyz.santeri.palmtree.data.remote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import rx.Observable;
import xyz.santeri.palmtree.base.DetailsService;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;

/**
 * @author Santeri Elo
 */
public class JsoupDetailsService implements DetailsService {
    private static final String DETAILS_URL = "http://naamapalmu.com/file/%s";

    @Override
    public Observable<ImageDetails> getImageDetails(int imageNumber) {
        return Observable.create(subscriber -> {
            Document doc = null;

            try {
                doc = Jsoup.connect(String.format(DETAILS_URL, imageNumber)).get();
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (doc == null || doc.select(String.format("div#hiddenfile-%s", imageNumber)).get(0) == null) {
                    subscriber.onError(new NullPointerException("Document is invalid"));
                }
            }

            assert doc != null;
            Element file = doc.select(String.format("div#hiddenfile-%s", imageNumber)).first();
            Element details = doc.select("div.fileinfo").first();

            boolean nsfw = false;
            ImageType imageType = ImageType.IMAGE;
            String fileUrl = null;
            String title = null;
            String rating = null;

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

            title = details.select("h4").first().text();

            rating = details.select(String.format("div.right > span#votecount-%s", imageNumber)).first().text();

            ImageDetails imageDetails = ImageDetails.create(imageNumber, fileUrl, imageType, nsfw, title, rating);

            subscriber.onNext(imageDetails);
            subscriber.onCompleted();
        });
    }
}