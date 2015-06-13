package andrzej.example.com.prefs;


import andrzej.example.com.utils.StringOperations;

public class APIEndpoints {

    public static final String WIKI_NAME = "http://pl.mlp.wikia.com";

    //Symbols
    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMEPERSAND = "&";

    //Search
    public static final String URL_SEARCH = WIKI_NAME + "/api/v1/Search/List"; //takes query, limit, minArticleQuality, batch and namespaces

    //Article
    public static final String URL_ARTICLE_CONTENT = WIKI_NAME + "/api/v1/Articles/AsSimpleJson"; //takes only id
    public static final String URL_ARTICLE_DETALIS = WIKI_NAME + "/api/v1/Articles/Details"; //takes ids

    //Related articles
    public static final String URL_RELATED_PAGES = WIKI_NAME + "/api/v1/RelatedPages/List"; //takes ids and limit

    //Suggested phrases for choosen query
    public static final String URL_SUGGESTED_PHRASES = WIKI_NAME + "/api/v1/SearchSuggestions/List"; //query

    //LastActivity
    public static final String URL_LAST_EDITED = WIKI_NAME + "/api/v1/Activity/RecentlyChangedArticles"; //takes limit
    public static final String URL_NEWEST = WIKI_NAME + "/api/v1/Articles/New"; //takies limit


    //Random Articles
    public static final String URL_RANDOM = WIKI_NAME + "/api/v1/Articles/List"; //takes limit(int), offset(str)

    //Query Strings
    public static final String URL_QUERY = "query=";
    public static final String URL_ID = "id=";
    public static final String URL_IDS = "ids=";
    public static final String URL_LIMIT = "limit=";
    public static final String URL_EXPAND = "expand=";
    public static final String URL_OFFSET = "offset=";
    public static final String URL_MIN_ARTICLE_QUALITY = "minArticleQuality=";
    public static final String URL_ALLOW_DUPLICATES = "allowDuplicates=";


    public static final String URL_BATCH = "batch=1";//1 is default
    public static final String URL_NAMESPACES = "namespaces=0%2C14";// 0,14 is default


    public static final String[] STOP_WORDS = {"/Galeria", "/Transkrypty", "Transkrypty/", "Kategoria:"}; //if search title contains one of these words don't show it to user


    public static String getUrlRandom(int limit){
        return URL_RANDOM +
                URL_CHAR_QUESTION +
                URL_EXPAND + 0 +
                URL_CHAR_AMEPERSAND +
                URL_LIMIT + limit +
                URL_CHAR_AMEPERSAND +
                URL_OFFSET + StringOperations.generateRandomString(3);
    }

    public static String getUrlSearch(String query, int limit) {
        return URL_SEARCH +
                URL_CHAR_QUESTION +
                URL_QUERY + query +
                URL_CHAR_AMEPERSAND +
                URL_LIMIT + Integer.toString(limit);
    }

    public static String getUrlItemContent(int id){
        return  URL_ARTICLE_CONTENT +
                URL_CHAR_QUESTION +
                URL_ID + String.valueOf(id);
    }

    public static String getUrlItemDetalis(int[] ids){
        String url = URL_ARTICLE_DETALIS +
                URL_CHAR_QUESTION +
                URL_IDS;
        for(int id : ids){
            url+=Integer.toString(id)+",";
        }
        return url;
    }

    public static String getLastEdited(int limit){
        return  URL_LAST_EDITED +
                URL_CHAR_QUESTION +
                URL_LIMIT + String.valueOf(limit) +
                URL_CHAR_AMEPERSAND +
                URL_ALLOW_DUPLICATES + String.valueOf(false);
    }

    public static String getNewestUrl(int limit){
        return  URL_NEWEST +
                URL_CHAR_QUESTION +
                URL_LIMIT + String.valueOf(limit);
    }
}
