package common.categories;

/**
 * This interface describes a category in junit tests. Anything marked with this category will not be
 * auto run when gradle builds as they are integration tests and take a while to run
 */
public interface GandalfIntegrationTestsCategory {
}
