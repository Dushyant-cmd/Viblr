LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PACKAGE_NAME := VeblrApp
LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_PRIVILEGED_MODULE := true3
LOCAL_PROGUARD_ENABLED := disabled
ifneq ($(call math_gt_or_eq, $(PLATFORM_SDK_VERSION), 28),)
LOCAL_PROPRIETARY_MODULE := true
endif
include $(BUILD_PACKAGE
)