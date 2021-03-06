/*
 * Copyright 2016 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.rank;

import android.support.annotation.StringRes;

import org.chaos.fx.cnbeta.R;
import org.chaos.fx.cnbeta.net.MobileApi;
import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.util.TimeStringHelper;
import org.chaos.fx.cnbeta.widget.ArticleHolder;
import org.chaos.fx.cnbeta.widget.BaseArticleAdapter;

class RankSubAdapter extends BaseArticleAdapter {

    private String mType;

    RankSubAdapter(String type) {
        mType = type;
    }

    @Override
    protected void convert(ArticleHolder holder, ArticleSummary summary) {
        super.convert(holder, summary);
        String subText = "";
        if (MobileApi.TYPE_COUNTER.equals(mType)) {
            subText = TimeStringHelper.getTimeStrByDefaultTimeStr(summary.getPublishTime());
        } else if (MobileApi.TYPE_DIG.equals(mType)) {
            subText = getSubText(R.string.read_count, summary.getCounter());
        } else if (MobileApi.TYPE_COMMENTS.equals(mType)) {
            subText = getSubText(R.string.comment_count, summary.getComment());
        }
        holder.summary.setText(subText);
    }

    private String getSubText(@StringRes int strId, int value) {
        return String.format(mContext.getString(strId), value);
    }
}