package xyz.santeri.palmtree.data.remote;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import rx.Observable;
import timber.log.Timber;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.data.model.TableImage;

/**
 * @author Santeri Elo
 */
public class JsoupListingService implements ListingService {
    private static final String FRONTPAGE_URL = "http://naamapalmu.com/listing/%s";
    private static final String LATEST_IMAGES_URL = "http://naamapalmu.com/filelist/images/latest/listing/%s";
    private static final String LATEST_VIDEOS_URL = "http://naamapalmu.com/filelist/videos/latest/table/%s";
    private static final String LATEST_ALL_URL = "http://naamapalmu.com/filelist/all/latest/table/%s";

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
                        doc = Jsoup.connect(String.format(LATEST_IMAGES_URL, pageNumber)).get();
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

    /**
     * Because table listings are a bit different, we need a different model object for tables.
     *
     * @param type       {@link ListingType} - only LATEST_VIDEOS and LATEST_ALL are supported
     * @param pageNumber Page number
     * @return {@link Observable} stream of {@link TableImage} objects
     */
    @Override
    public Observable<TableImage> getTableListing(ListingType type, int pageNumber) {
        return Observable.create(subscriber -> {
            Document doc = null;

            try {
                switch (type) {
                    case FRONT_PAGE:
                        subscriber.onError(new UnsupportedOperationException("Only listing types LATEST_VIDEOS or LATEST_ALL are supported"));
                        break;
                    case LATEST_IMAGES:
                        subscriber.onError(new UnsupportedOperationException("Only listing types LATEST_VIDEOS or LATEST_ALL are supported"));
                        break;
                    case LATEST_VIDEOS:
                        doc = Jsoup.connect(String.format(LATEST_VIDEOS_URL, pageNumber)).get();
                        break;
                    case LATEST_ALL:
                        doc = Jsoup.connect(String.format(LATEST_ALL_URL, pageNumber)).get();
                        break;
                    default:
                        doc = Jsoup.connect(String.format(LATEST_ALL_URL, pageNumber)).get();
                        break;
                }
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (doc == null || doc.select("tbody > tr") == null) {
                    subscriber.onError(new NullPointerException("Document is invalid"));
                }
            }

            assert doc != null;
            Elements files = doc.select("table.filelist > tbody").first().getElementsByTag("td");

            Timber.d("Found %s files in table", files.size());

            for (Element file : files) {
                int id;
                boolean nsfw = false;
                String thumbnailUrl = null;
                String title = null;

                title = file.select("a > span").first().text();

                if (file.select("a > img").first().attr("src").contains("nsfw.png")) {
                    nsfw = true;
                }

                thumbnailUrl = file.select("a > img").first().attr("src");

                id = Integer.parseInt(file.select("a").first().attr("href").split("/")[4]);

                TableImage image = TableImage.create(id, thumbnailUrl, nsfw, title);
                Timber.d("File %s: '%s", files.indexOf(file), image.toString());
                subscriber.onNext(image);
            }

            subscriber.onCompleted();
        });
    }
}
