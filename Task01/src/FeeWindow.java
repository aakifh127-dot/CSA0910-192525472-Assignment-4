import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * View only: collects input, shows results. Each button has its own small listener that calls the FeeService.
 * The payment runs on a worker thread, so the window never freezes (the original slept on the event thread).
 */
public class FeeWindow extends Frame {
    private final TextField regNo = new TextField(12);
    private final TextField amount = new TextField(10);
    private final Button pay = new Button("Pay");
    private final Button clear = new Button("Clear");
    private final Button receipt = new Button("Receipt");
    private final Label status = new Label("Enter register number and amount");
    private final FeeService service;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    public FeeWindow(FeeService service) {
        super("Fee Payment");
        this.service = service;
        Panel form = new Panel(new GridLayout(2, 2, 6, 6));
        form.add(new Label("Register No:")); form.add(regNo);
        form.add(new Label("Amount (Rs.):")); form.add(amount);
        Panel buttons = new Panel();
        buttons.add(pay); buttons.add(clear); buttons.add(receipt);
        add(form, "North"); add(buttons, "Center"); add(status, "South");

        pay.addActionListener(e -> doPay());
        clear.addActionListener(e -> { regNo.setText(""); amount.setText(""); show("Cleared"); });
        receipt.addActionListener(e -> doReceipt());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { worker.shutdown(); dispose(); }
        });
        pack();
    }

    private void doPay() {
        String reg = regNo.getText(), amt = amount.getText();
        pay.setEnabled(false);
        show("Processing payment...");
        worker.submit(() -> {
            String message;
            try { message = "Paid: " + service.pay(reg, amt); }
            catch (FeeException ex) { message = ex.getMessage(); }
            String finalMessage = message;
            EventQueue.invokeLater(() -> { show(finalMessage); pay.setEnabled(true); });
        });
    }

    private void doReceipt() {
        String reg = regNo.getText();
        worker.submit(() -> {
            String message;
            try { message = service.lastReceipt(reg).toString(); }
            catch (FeeException ex) { message = ex.getMessage(); }
            String finalMessage = message;
            EventQueue.invokeLater(() -> show(finalMessage));
        });
    }

    private void show(String text) { status.setText(text); }

    public static void main(String[] args) {
        FeeService service = new FeeService(new InMemoryFeeRepository(1500));
        EventQueue.invokeLater(() -> new FeeWindow(service).setVisible(true));
    }
}
