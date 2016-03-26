package com.github.daltonks.engine.util;

public class SoundHandler {
    /*
    private static AudioManager audioManager;
    private static SoundPool pool;
    private static ConcurrentHashMap<String, Integer> soundMap = new ConcurrentHashMap<>();

    public static void init(Context context) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            createOldSoundPool();
        } else {
            createNewSoundPool();
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(soundMap.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    for(Util.ResourceAndShortenedName rasn : Util.getAllResourcesThatStartWith("sound")) {
                        soundMap.put(rasn.shortenedName, pool.load(Engine.INSTANCE, rasn.resourceID, 1));
                    }
                }
            });
            thread.start();
        }
    }

    @SuppressWarnings("deprecation")
    private static void createOldSoundPool() {
        pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        pool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(4)
                .build();
    }

    private static final float ZERO_SOUND_DISTANCE = 10000;
    private static final float MINIMUM_EAR_MULT = .4f;
    public static void playSound(String name, Vec3d location) {
        Integer soundID = soundMap.get(name);
        if(soundID == null) {
            return;
        }

        Camera camera = Engine.INSTANCE.getCurrentSubActivity().getEngineWorld().getCamera();
        Vec3d cameraLoc = camera.getViewMatrix().getLocation();

        Vec3d offsets = location.clone().sub(cameraLoc);

        float[] viewMatrix = camera.getViewMatrix().getMatrix();
        Vec3d offsetToViewRotation = offsets.clone().multMatrix(viewMatrix);
        offsetToViewRotation.normalize();

        float distance = (float) offsets.length();

        float distanceMult = Math.max(0, 1 - distance / ZERO_SOUND_DISTANCE);
        float currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        float zeroOneBounded = (float) ((offsetToViewRotation.x() + 1) / 2);
        float leftMult = (1 - zeroOneBounded) * (1 - MINIMUM_EAR_MULT) + MINIMUM_EAR_MULT;
        float rightMult = zeroOneBounded * (1 - MINIMUM_EAR_MULT) + MINIMUM_EAR_MULT;
        float leftVolume = leftMult * distanceMult * currentVolume / maxVolume;
        float rightVolume = rightMult * distanceMult * currentVolume / maxVolume;
        int priority = 1;
        int loops = 0;
        float playbackRate = 1f;
        pool.play(soundID, leftVolume, rightVolume, priority, loops, playbackRate);

        Pools.recycle(offsetToViewRotation);
        Pools.recycle(offsets);
    }*/
}