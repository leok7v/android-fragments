/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fragments.pager;

import android.app.Fragment;

import java.lang.reflect.*;

/**
 * Helper for accessing features in {@link Fragment} introduced after
 * API level 13 in a backwards compatible fashion.
 */
public class FragmentCompat {

    interface FragmentCompatImpl {
        void setMenuVisibility(Fragment f, boolean visible);
        void setUserVisibleHint(Fragment f, boolean deferStart);
    }


    private static class FragmentCompatICS {
        static void setMenuVisibility(Fragment f, boolean visible) {
            f.setMenuVisibility(visible);
        }
    }

    private static class FragmentCompatICSMR1 {

        private static Method setUserVisibleHint;

        static void setUserVisibleHint(Fragment f, boolean isVisible) {
            // f.setUserVisibleHint(isVisible);
            try {
                if (setUserVisibleHint == null) {
                    setUserVisibleHint = f.getClass().getMethod("setUserVisibleHint", new Class[]{boolean.class});
                }
                setUserVisibleHint.invoke(f, new Object[]{isVisible});
            } catch (Throwable e) {
                throw new Error(e);
            }
        }
    }

    private static class BaseFragmentCompatImpl implements FragmentCompatImpl {
        public void setMenuVisibility(Fragment f, boolean visible) {
        }
        public void setUserVisibleHint(Fragment f, boolean deferStart) {
        }
    }

    private static class ICSFragmentCompatImpl extends BaseFragmentCompatImpl {
        @Override
        public void setMenuVisibility(Fragment f, boolean visible) {
            FragmentCompatICS.setMenuVisibility(f, visible);
        }
    }

    static class ICSMR1FragmentCompatImpl extends ICSFragmentCompatImpl {
        @Override
        public void setUserVisibleHint(Fragment f, boolean deferStart) {
            FragmentCompatICSMR1.setUserVisibleHint(f, deferStart);
        }
    }

    private static final FragmentCompatImpl IMPL;

    static {
        if (android.os.Build.VERSION.SDK_INT >= 15) {
            IMPL = new ICSMR1FragmentCompatImpl();
        } else if (android.os.Build.VERSION.SDK_INT >= 14) {
            IMPL = new ICSFragmentCompatImpl();
        } else {
            IMPL = new BaseFragmentCompatImpl();
        }
    }

    /**
     * Call {@link Fragment#setMenuVisibility(boolean) Fragment.setMenuVisibility(boolean)}
     * if running on an appropriate version of the platform.
     */
    public static void setMenuVisibility(Fragment f, boolean visible) {
        IMPL.setMenuVisibility(f, visible);
    }

    /**
     * Call {@link Fragment#setUserVisibleHint(boolean) setUserVisibleHint(boolean)}
     * if running on an appropriate version of the platform.
     */
    public static void setUserVisibleHint(Fragment f, boolean deferStart) {
        IMPL.setUserVisibleHint(f, deferStart);
    }
}
