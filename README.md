[![](https://jitpack.io/v/sotware-supremacy/general-location-service.svg)](https://jitpack.io/#sotware-supremacy/general-location-service)

# General Location Service (GLS)
> **for Google & Huawei**

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Step 2. Add the dependency

```gradle
dependencies {
	        implementation 'com.github.sotware-supremacy:general-location-service:<VERSION>'
}
```

## Usage
> **put this in AndroidManifest.xml**
```xml
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

```kotlin
...
import gls.di.GLSInitializer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
	
	// initialize service 
	val glsManager = GLSInitializer(applicationContext).create()

	CoroutineScope(Dispatchers.IO).launch{
            with(glsManager) {
                Log.d("TESTING", "is Google Or Huawei Service Available: $isServicesAvailable")
		
		// Collect location updates as flow
                requestLocationUpdatesAsFlow().collect {
                    // Do Something
                }
		
		// OR use it as suspend function
		val locations = requestLocationUpdatesw()
		Log.d("TESTING", "Locations: $locations")
            }
        }
    }
}
```

```kotlin
// Also can configure the location request for location updates

// first import Priority class
import gls.domain.models.Priority

// Then just use the `configureLocationRequest` method
CoroutineScope(Dispatchers.IO).launch{
        with(glsManager) {
            configureLocationRequest(
                intervalMillis = 3000,
                priority = Priority.BalancedPowerAccuracy,
                maxUpdates = 3
            )
            
            requestLocationUpdatesAsFlow().collect{
                // Do something
            }
    }
}
```
