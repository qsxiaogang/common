package com.ccclubs.common.adapter.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * Methods about layout manager.
 */
interface ILayoutManager {
    boolean hasLayoutManager();

    RecyclerView.LayoutManager getLayoutManager();
}
