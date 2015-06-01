package andrzej.example.com.prefs;

/**
 * Created by andrzej on 01.06.15.
 */
public class APIEndpoints {

    public static final String WIKI_NAME = "http://pl.mlp.wikia.com";

    //Symbols
    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMEPERSAND = "&";

    //Search
    public static final String URL_SEARCH = WIKI_NAME+"/api/v1/Search/List"; //takes query, limit, minArticleQuality, batch and namespaces

    //Article
    public static final String URL_ARTICLE_CONTENT = WIKI_NAME+"/api/v1/Articles/AsSimpleJson"; //takes only id
    public static final String URL_ARTICLE_DETALIS = WIKI_NAME+"/api/v1/Articles/Details?ids=80541&abstract=100&width=200&height=200"; //takes ids

    //Related articles
    public static final String URL_RELATED_PAGES = WIKI_NAME+"/api/v1/RelatedPages/List"; //takes ids and limit

    //Suggested phrases for choosen query
    public static final String URL_SUGGESTED_PHRASES = WIKI_NAME+"/api/v1/SearchSuggestions/List"; //query



    //Query Strings
    public static final String URL_QUERY = "query=";
    public static final String URL_ID = "id=";
    public static final String URL_IDS = "ids=";
    public static final String URL_LIMIT = "limit=";
    public static final String URL_MIN_ARTICLE_QUALITY = "minArticleQuality=";


    public static final String URL_BATCH = "batch=1";//1 is default
    public static final String URL_NAMESPACES = "namespaces=0%2C14";// 0,14 is default
}
