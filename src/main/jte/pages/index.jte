@template.layout(content = @`
    <div class="p-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2 tracking-tight">Verdict Engine</h1>
        <p class="text-gray-500 mb-8 text-sm">Input facts. Get one decision. No noise.</p>

        <form action="/home-repair/calculate" method="POST" class="space-y-6">
            <div>
                <label class="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Zip Code</label>
                <input type="text" name="zipCode" required 
                       class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent transition"
                       placeholder="e.g. 90210">
            </div>

            <div>
                <label class="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Estimated Budget ($)</label>
                <input type="text" name="budget" required 
                       class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent transition"
                       placeholder="e.g. 5000">
            </div>

            <div>
                <label class="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Construction Decade</label>
                <select name="decade" required 
                        class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent transition">
                    <option value="" disabled selected>Select Year/Decade</option>
                    <option value="2020s">2020s</option>
                    <option value="2010s">2010s</option>
                    <option value="2000s">2000s</option>
                    <option value="1990s">1990s</option>
                    <option value="1980s">1980s</option>
                    <option value="Pre-1980">Pre-1980</option>
                </select>
            </div>

            <div>
                <label class="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">Primary Goal</label>
                <select name="purpose" required 
                        class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-black focus:border-transparent transition">
                    <option value="" disabled selected>Select Purpose</option>
                    <option value="Flip/Sell">Flip & Sell (Profit)</option>
                    <option value="Live In">Live In (Comfort)</option>
                    <option value="Rent Out">Rent Out (Tenant Proofing)</option>
                    <option value="Emergency">Emergency Fix</option>
                </select>
            </div>

            <button type="submit" 
                    class="w-full bg-black text-white font-bold py-4 rounded-lg hover:bg-gray-800 transition active:scale-95 transform duration-150 shadow-lg mt-4">
                GET VERDICT
            </button>
            
            <input type="hidden" name="userEmail" value="anonymous@example.com"> <!-- Placeholder for MVP -->
        </form>
    </div>
`)
