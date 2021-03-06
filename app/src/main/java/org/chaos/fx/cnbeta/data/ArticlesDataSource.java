/*
 * Copyright 2017 Chaos
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

package org.chaos.fx.cnbeta.data;

import android.support.annotation.NonNull;

import org.chaos.fx.cnbeta.net.model.ArticleSummary;
import org.chaos.fx.cnbeta.net.model.HasReadArticle;

import java.util.List;

/**
 * @author Chaos
 *         01/03/2017
 */

public interface ArticlesDataSource {

    interface LoadSummaryCallback {
        void onTasksLoaded(List<ArticleSummary> summaries);

        void onDataNotAvailable();
    }

    void getSummaries(@NonNull LoadSummaryCallback callback);

    void saveSummaries(@NonNull List<ArticleSummary> summaries);

    void readArticle(@NonNull HasReadArticle article);

    void readArticle(int sid);

    boolean hasReadArticle(@NonNull ArticleSummary summary);

    boolean hasReadArticle(int sid);

    void deleteAllSummaries();
}
