package com.socialnetwork.socialnetworkapp.repository;

import com.socialnetwork.socialnetworkapp.domain.Friendship;
import com.socialnetwork.socialnetworkapp.domain.Tuple;
import com.socialnetwork.socialnetworkapp.utils.dto.FriendshipFilterDTO;
import com.socialnetwork.socialnetworkapp.utils.paging.Page;
import com.socialnetwork.socialnetworkapp.utils.paging.Pageable;

public interface FriendshipRepository extends PagingRepository<Tuple<Long, Long>, Friendship> {
    Page<Friendship> findAllOnPage(Pageable pageable, FriendshipFilterDTO filter);
}
