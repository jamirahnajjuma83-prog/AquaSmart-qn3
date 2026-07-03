import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SmartMeterTest {

    @Test
    public void testLoadTokenReopensValve() {
        // Create meter with zero credit - valve should be closed initially
        SmartMeter meter = new SmartMeter("SM001", 0);
        
        // Try to consume water - should fail and close valve
        meter.recordConsumption(1);
        assertFalse(meter.isValveOpen(), "Valve should be closed after failed consumption");
        
        // Load token - should reopen valve
        meter.loadToken(500);
        assertTrue(meter.isValveOpen(), "Valve should be open after loading token");
        assertEquals(500, meter.getCreditBalance(), 0.001, "Balance should be exactly 500");
    }

    @Test
    public void testConsumptionClosesValve() {
        // Create meter with UGX 100 credit
        SmartMeter meter = new SmartMeter("SM002", 100);
        
        // Initially valve should be open
        assertTrue(meter.isValveOpen(), "Valve should be open initially");
        
        // Consume 5 litres - costs UGX 250 (5 * 50), which exceeds UGX 100
        // This should close the valve
        boolean result = meter.recordConsumption(5);
        
        assertFalse(result, "Consumption should return false when insufficient credit");
        assertFalse(meter.isValveOpen(), "Valve should be closed when credit exhausted");
        assertEquals(0, meter.getCreditBalance(), 0.001, "Balance should be 0");
    }
    
    @Test
    public void testNormalConsumption() {
        // Create meter with UGX 1000 credit
        SmartMeter meter = new SmartMeter("SM003", 1000);
        
        // Consume 10 litres - costs UGX 500 (10 * 50)
        boolean result = meter.recordConsumption(10);
        
        assertTrue(result, "Consumption should return true when enough credit");
        assertTrue(meter.isValveOpen(), "Valve should remain open");
        assertEquals(500, meter.getCreditBalance(), 0.001, "Balance should be UGX 500");
    }
    
    @Test
    public void testExactLimitConsumption() {
        // Create meter with exactly UGX 250 credit
        SmartMeter meter = new SmartMeter("SM004", 250);
        
        // Consume 5 litres - costs exactly UGX 250
        boolean result = meter.recordConsumption(5);
        
        assertTrue(result, "Consumption should succeed when exact amount available");
        assertTrue(meter.isValveOpen(), "Valve should remain open at zero balance");
        assertEquals(0, meter.getCreditBalance(), 0.001, "Balance should be exactly 0");
    }
    
    @Test
    public void testMultipleConsumptions() {
        // Create meter with UGX 300 credit
        SmartMeter meter = new SmartMeter("SM005", 300);
        
        // First consumption - 3 litres (UGX 150)
        assertTrue(meter.recordConsumption(3));
        assertEquals(150, meter.getCreditBalance(), 0.001);
        assertTrue(meter.isValveOpen());
        
        // Second consumption - 2 litres (UGX 100)
        assertTrue(meter.recordConsumption(2));
        assertEquals(50, meter.getCreditBalance(), 0.001);
        assertTrue(meter.isValveOpen());
        
        // Third consumption - 2 litres (UGX 100) - should fail
        assertFalse(meter.recordConsumption(2));
        assertEquals(0, meter.getCreditBalance(), 0.001);
        assertFalse(meter.isValveOpen());
    }
}
