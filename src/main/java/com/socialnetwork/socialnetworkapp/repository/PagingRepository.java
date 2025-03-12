package com.socialnetwork.socialnetworkapp.repository;

import com.socialnetwork.socialnetworkapp.domain.Entity;
import com.socialnetwork.socialnetworkapp.utils.paging.Page;
import com.socialnetwork.socialnetworkapp.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}
