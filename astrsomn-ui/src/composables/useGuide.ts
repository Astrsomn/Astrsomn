import {ref} from 'vue'
import {driver} from 'driver.js'
import 'driver.js/dist/driver.css'
import {guideConfig, type GuideStep, isGuideCompleted, setGuideCompleted} from '@/config/guide-config'

let driverInstance: ReturnType<typeof driver> | null = null

const isGuideActive = ref(false)

function createDriver(steps: GuideStep[]): ReturnType<typeof driver> {
    if (driverInstance) {
        driverInstance.destroy()
    }

    driverInstance = driver({
        animate: true,
        overlayOpacity: 0.75,
        stagePadding: 10,
        allowClose: true,
        overlayClickBehavior: 'close',
        showProgress: true,
        showButtons: ['next', 'previous', 'close'],
        onDestroyed: () => {
            isGuideActive.value = false
            setGuideCompleted()
        }
    })

    driverInstance.setSteps(steps.map(step => ({
        element: step.element,
        popover: {
            title: step.popover.title,
            description: step.popover.description,
            position: step.popover.position || 'bottom'
        }
    })))

    return driverInstance
}

function startGuide(steps?: GuideStep[]) {
    if (!guideConfig.enabled) {
        console.warn('Guide is disabled')
        return
    }

    const stepsToUse = steps || guideConfig.steps
    if (stepsToUse.length === 0) {
        console.warn('No guide steps defined')
        return
    }

    const driverObj = createDriver(stepsToUse)
    isGuideActive.value = true
    driverObj.drive()
}

function startGuideForElement(element: string, title: string, description: string) {
    if (!guideConfig.enabled) return

    const driverObj = createDriver([{
        id: 'temp-guide',
        element,
        popover: {
            title,
            description,
            position: 'bottom'
        }
    }])
    isGuideActive.value = true
    driverObj.drive()
}

function stopGuide() {
    if (driverInstance) {
        driverInstance.destroy()
        driverInstance = null
    }
    isGuideActive.value = false
}

function completeGuide() {
    stopGuide()
    setGuideCompleted()
}

function isLoggedIn(): boolean {
    return !!localStorage.getItem('token')
}

function initAutoGuide() {
    if (!isLoggedIn()) return
    if (guideConfig.autoStart && !isGuideCompleted()) {
        setTimeout(() => {
            startGuide()
        }, 500)
    }
}

/** 登录后初始化引导 — 仅在已登录且引导未完成时启动 */
function initGuideAfterLogin(delay = 800) {
    if (!isLoggedIn()) {
        console.warn('Guide: user not logged in, skipping')
        return
    }
    if (!guideConfig.enabled) {
        console.warn('Guide: disabled by config')
        return
    }
    if (isGuideCompleted()) {
        return
    }
    setTimeout(() => {
        startGuide()
    }, delay)
}

export function useGuide() {
    return {
        isGuideActive,
        startGuide,
        startGuideForElement,
        stopGuide,
        completeGuide,
        initAutoGuide,
        initGuideAfterLogin,
        isLoggedIn
    }
}
