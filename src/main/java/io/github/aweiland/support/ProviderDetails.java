package io.github.aweiland.support;

/**
 * User details from OAuth providers
 */
public class ProviderDetails {

    private final String provider;

    private final String providerId;

    private final String displayName;

    private final String profileUri;

    private final String firstName;

    private final String lastName;

    private final String email;

    private final Integer age;

    private final String city;

    private final String photoUri;

    private final String thumbnailUri;

    private ProviderDetails(Builder builder) {
        provider = builder.provider;
        providerId = builder.providerId;
        displayName = builder.displayName;
        profileUri = builder.profileUri;
        firstName = builder.firstName;
        lastName = builder.lastName;
        email = builder.email;
        age = builder.age;
        city = builder.city;
        photoUri = builder.photoUri;
        thumbnailUri = builder.thumbnailUri;
    }


    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }


    public static final class Builder {
        private String provider;
        private String providerId;
        private String displayName;
        private String profileUri;
        private String firstName;
        private String lastName;
        private String email;
        private Integer age;
        private String city;
        private String photoUri;
        private String thumbnailUri;

        public Builder() {
        }

        public Builder(ProviderDetails copy) {
            this.provider = copy.provider;
            this.providerId = copy.providerId;
            this.displayName = copy.displayName;
            this.profileUri = copy.profileUri;
            this.firstName = copy.firstName;
            this.lastName = copy.lastName;
            this.email = copy.email;
            this.age = copy.age;
            this.city = copy.city;
            this.photoUri = copy.photoUri;
            this.thumbnailUri = copy.thumbnailUri;
        }

        public Builder provider(String val) {
            provider = val;
            return this;
        }

        public Builder providerId(String val) {
            providerId = val;
            return this;
        }

        public Builder displayName(String val) {
            displayName = val;
            return this;
        }

        public Builder profileUri(String val) {
            profileUri = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder age(Integer val) {
            age = val;
            return this;
        }

        public Builder city(String val) {
            city = val;
            return this;
        }

        public Builder photoUri(String val) {
            photoUri = val;
            return this;
        }

        public Builder thumbnailUri(String val) {
            thumbnailUri = val;
            return this;
        }

        public ProviderDetails build() {
            return new ProviderDetails(this);
        }
    }
}
