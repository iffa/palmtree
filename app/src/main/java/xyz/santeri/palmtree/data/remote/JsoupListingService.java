package xyz.santeri.palmtree.data.remote;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.base.ListingType;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;

/**
 * @author Santeri Elo
 */
public class JsoupListingService implements ListingService {
    private static final String FRONTPAGE_URL = "http://naamapalmu.com/listing/%s";
    private static final String LATEST_IMAGES_URL = "http://naamapalmu.com/filelist/images/latest/listing/%s";
    private static final String LATEST_VIDEOS_URL = "http://naamapalmu.com/filelist/videos/latest/table/%s";
    private static final String LATEST_ALL_URL = "http://naamapalmu.com/filelist/all/latest/table/%s";
    private static final String RANDOM_URL = "http://naamapalmu.com/filelist/all/random/table/%s";

    @NonNull
    @Override
    public Observable<ImageDetails> getListing(@NonNull ListingType type, int pageNumber) {
        return Observable.create(subscriber -> {
            Document doc = null;

            try {
                switch (type) {
                    case FRONT_PAGE:
                        doc = Jsoup.connect(String.format(FRONTPAGE_URL, pageNumber)).get();
                        break;
                    case LATEST_IMAGES:
                        doc = Jsoup.connect(String.format(LATEST_IMAGES_URL, pageNumber)).get();
                        break;
                    case LATEST_VIDEOS:
                        doc = Jsoup.connect(String.format(LATEST_VIDEOS_URL, pageNumber)).get();
                        break;
                    case LATEST_ALL:
                        doc = Jsoup.connect(String.format(LATEST_ALL_URL, pageNumber)).get();
                        break;
                    case RANDOM:
                        doc = Jsoup.connect(String.format(RANDOM_URL, pageNumber)).get();
                        break;
                    default:
                        doc = Jsoup.connect(String.format(FRONTPAGE_URL, pageNumber)).get();
                        break;
                }
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (doc == null) {
                    subscriber.onError(new NullPointerException("Document is invalid"));
                }
            }

            assert doc != null;

            if (type == ListingType.FRONT_PAGE || type == ListingType.LATEST_IMAGES) {
                Elements files = doc.select("div.file");

                for (Element file : files) {
                    int id;
                    boolean nsfw = false;
                    ImageType imageType = ImageType.IMAGE;
                    String fileUrl = null;
                    String title = null;
                    String rating = null;
                    String description = null;

                    title = file.select("p.filetitle").first().text();

                    if (file.select("pre").size() > 0) {
                        description = file.select("pre").first().text();
                    }

                    if (file.select("p.nsfwwarning").size() > 0) {
                        nsfw = true;
                    }

                    if (file.select("a > img").size() > 0) {
                        fileUrl = "http:" + file.select("a > img").first().attr("src");
                        imageType = ImageType.IMAGE;
                    } else if (file.select("video > source").size() > 0) {
                        fileUrl = "http:" + file.select("video > source").first().attr("src");
                        imageType = ImageType.VIDEO;
                    } else {
                        throw new UnsupportedOperationException("Only images or videos are supported");
                    }

                    id = Integer.parseInt(file.select("p.filetitle > a").first().attr("href").split("/")[4]);

                    rating = file.select("div.listingcomments > span").first().text();

                    ImageDetails imageDetails = ImageDetails.create(id, fileUrl, imageType, nsfw, title, rating, description, null);
                    subscriber.onNext(imageDetails);
                }
            } else {
                Elements files = doc.select("table.filelist > tbody").first().getElementsByTag("td");

                for (Element file : files) {
                    int id;
                    boolean nsfw = false;
                    String thumbnailUrl = null;
                    String title = null;

                    title = file.select("a > span").first().text();

                    if (file.select("a > img").first().attr("src").contains("nsfw.png")) {
                        nsfw = true;
                    }

                    thumbnailUrl = "http:" + file.select("a > img").first().attr("src");

                    id = Integer.parseInt(file.select("a").first().attr("href").split("/")[4]);

                    ImageDetails image = ImageDetails.create(id, thumbnailUrl, ImageType.UNDEFINED, nsfw, title, null, null, null);
                    subscriber.onNext(image);
                }
            }

            subscriber.onCompleted();
        });
    }
}