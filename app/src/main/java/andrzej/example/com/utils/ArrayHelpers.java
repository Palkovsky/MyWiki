package andrzej.example.com.utils;

import java.util.List;

import andrzej.example.com.models.ArticleHeader;

/**
 * Created by andrzej on 11.06.15.
 */
public class ArrayHelpers {
    public static List<ArticleHeader> headersRemoveLevels(List<ArticleHeader> mData){

        for(int i = 0; i<mData.size(); i++){
            if(mData.get(i).getLevel()!=2 && mData.get(i).getLevel() != 3 && mData.get(i).getLevel()!=1)
                mData.remove(i);
        }
        return mData;
    }
}
