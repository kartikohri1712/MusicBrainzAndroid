package org.metabrainz.mobile.data.di

import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.Binds
import dagger.Module
import org.metabrainz.mobile.data.repository.CollectionRepository
import org.metabrainz.mobile.data.repository.CollectionRepositoryImpl
import org.metabrainz.mobile.data.repository.LookupRepositoryImpl
import org.metabrainz.mobile.data.repository.LookupRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class CollectionRepositoryModule {
    @Binds
    abstract fun bindsCollectionRepository(repository: CollectionRepositoryImpl?): CollectionRepository?
}